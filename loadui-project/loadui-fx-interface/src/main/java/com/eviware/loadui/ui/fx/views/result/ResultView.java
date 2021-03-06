/*
 * Copyright 2013 SmartBear Software
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the Licence for the specific language governing permissions and limitations
 * under the Licence.
 */
package com.eviware.loadui.ui.fx.views.result;

import static com.eviware.loadui.ui.fx.util.ObservableLists.fx;
import static com.eviware.loadui.ui.fx.util.ObservableLists.transform;
import static javafx.beans.binding.Bindings.bindContent;

import java.io.Closeable;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eviware.loadui.api.statistics.store.Execution;
import com.eviware.loadui.ui.fx.api.input.DraggableEvent;
import com.eviware.loadui.ui.fx.control.PageList;
import com.eviware.loadui.ui.fx.util.FXMLUtils;
import com.google.common.base.Function;

public class ResultView extends StackPane
{
	enum ExecutionState
	{
		RECENT( "result" ), ARCHIVED( "archive" );

		private String idPrefix;

		private ExecutionState( String idPrefix )
		{
			this.idPrefix = idPrefix;
		}
	};

	protected static final Logger log = LoggerFactory.getLogger( ResultView.class );

	@FXML
	private PageList<ExecutionView> resultNodeList;

	@FXML
	private PageList<ExecutionView> archiveNodeList;

	private final ObservableList<Execution> recentExList;
	private ObservableList<ExecutionView> recentExViews;

	private final ObservableList<Execution> archivedExList;
	private ObservableList<ExecutionView> archivedExViews;
	
	private final Closeable toClose;

	public ResultView( ObservableList<Execution> recentExecutions, ObservableList<Execution> archivedExecutions, Closeable toClose )
	{
		this.recentExList = recentExecutions;
		this.archivedExList = archivedExecutions;
		this.toClose = toClose;
		
		FXMLUtils.load( this );
	}

	@FXML
	private void initialize()
	{
		
		recentExViews = createExecutionViewsFor( recentExList, ExecutionState.RECENT );
		bindContent( resultNodeList.getItems(), recentExViews );

		archivedExViews = createExecutionViewsFor( archivedExList, ExecutionState.ARCHIVED );
		bindContent( archiveNodeList.getItems(), archivedExViews );

		initArchiveNodeList();

	}

	private ObservableList<ExecutionView> createExecutionViewsFor( final ObservableList<Execution> executions,
			final ExecutionState state )
	{
		final ObservableList<ExecutionView> result = fx( transform( executions, new Function<Execution, ExecutionView>()
		{
			@Override
			public ExecutionView apply( Execution e )
			{
				ExecutionView view = new ExecutionView( e, state, toClose );
				view.setId( idFor( state, executions.indexOf( e ) ) );
				return view;
			}
		} ) );

		result.addListener( new ListChangeListener<ExecutionView>()
		{
			@Override
			public void onChanged( Change<? extends ExecutionView> c )
			{
				for( ExecutionView e : result )
					e.setId( idFor( state, result.indexOf( e ) ) );
			}

		} );
		return result;
	}

	private static String idFor( final ExecutionState state, int index )
	{
		return state.idPrefix + "-" + index;
	}

	private void initArchiveNodeList()
	{
		archiveNodeList.addEventHandler( DraggableEvent.ANY, new EventHandler<DraggableEvent>()
		{
			@Override
			public void handle( DraggableEvent event )
			{
				if( event.getData() instanceof Execution )
				{
					Execution execution = ( Execution )event.getData();

					if( event.getEventType() == DraggableEvent.DRAGGABLE_ENTERED )
					{
						if( !execution.isArchived() )
							event.accept();
					}
					else if( event.getEventType() == DraggableEvent.DRAGGABLE_DROPPED )
					{
						log.info( "Archiving test run results" );
						execution.archive();
					}
				}
			}
		} );

	}
}
