package com.eviware.loadui.ui.fx.views.analysis;

import static com.eviware.loadui.ui.fx.util.TreeUtils.dummyItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eviware.loadui.api.model.AgentItem;
import com.eviware.loadui.api.statistics.Statistic;
import com.eviware.loadui.api.statistics.StatisticHolder;
import com.eviware.loadui.api.statistics.StatisticVariable;
import com.eviware.loadui.api.traits.Labeled;
import com.eviware.loadui.ui.fx.control.fields.Validatable;
import com.eviware.loadui.ui.fx.views.assertions.LabeledTreeCell;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

public class StatisticTree extends TreeView<Labeled> implements Validatable
{
	public static final String AGENT_TOTAL = "Total";

	protected static final Logger log = LoggerFactory.getLogger( StatisticTree.class );

	private final BooleanProperty isValidProperty = new SimpleBooleanProperty( false );

	// Used to prevent unwanted chain reactions when forcing TreeItems to collapse. 
	public final AtomicBoolean isForceCollapsing = new AtomicBoolean( false );

	private final ImmutableCollection<AgentItem> agents;

	public static StatisticTree forHolder( StatisticHolder holder )
	{
		TreeItem<Labeled> root = new TreeItem<Labeled>( holder );
		return new StatisticTree( holder, root );
	}

	private StatisticTree( StatisticHolder holder, TreeItem<Labeled> root )
	{
		super( root );
		setShowRoot( false );
		getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
		getStyleClass().add( "assertable-tree" );

		agents = ImmutableList.copyOf( holder.getCanvas().getProject().getWorkspace().getAgents() );

		addVariablesToTree( holder, root );

		getSelectionModel().selectedItemProperty().addListener( new ChangeListener<TreeItem<Labeled>>()
		{
			@Override
			public void changed( ObservableValue<? extends TreeItem<Labeled>> arg0, TreeItem<Labeled> oldValue,
					TreeItem<Labeled> newValue )
			{
				if( isForceCollapsing.get() )
					return;
				if( newValue == null || newValue.getValue() == null )
				{
					isValidProperty.set( false );
				}
				else
				{
					if( !newValue.isLeaf() )
					{
						newValue.setExpanded( true );
						getSelectionModel().clearSelection();
						isValidProperty.set( false );
					}
					else
					{
						isValidProperty.set( true );
					}
				}
				log.debug( "getSelectionModel().getSelectedItems(): " + getSelectionModel().getSelectedItems() );
			}
		} );

		setCellFactory( new Callback<TreeView<Labeled>, TreeCell<Labeled>>()
		{
			@Override
			public TreeCell<Labeled> call( TreeView<Labeled> treeView )
			{
				return new LabeledTreeCell();
			}
		} );
	}

	private void addVariablesToTree( StatisticHolder holder, TreeItem<Labeled> root )
	{
		for( String variableName : holder.getStatisticVariableNames() )
		{
			StatisticVariable variable = holder.getStatisticVariable( variableName );
			final TreeItem<Labeled> variableItem = treeItem( variable, root );

			for( String statisticName : variable.getStatisticNames() )
			{
				Statistic<?> statistic = variable.getStatistic( statisticName, StatisticVariable.MAIN_SOURCE );
				final TreeItem<Labeled> statisticItem = treeItem( statistic, variableItem );
				if( !agents.isEmpty() )
				{
					statisticItem.getChildren().add( dummyItem( AGENT_TOTAL, StatisticVariable.MAIN_SOURCE ) );
				}
				for( AgentItem agent : agents )
				{
					statisticItem.getChildren().add( new TreeItem<Labeled>( agent ) );
				}
			}
		}
	}

	@Override
	public ReadOnlyBooleanProperty isValidProperty()
	{
		return isValidProperty;
	}

	@Override
	public boolean isValid()
	{
		return isValidProperty.get();
	}

	private int getDepth()
	{
		int depth = 3;
		if( !agents.isEmpty() )
			depth++ ;
		return depth;
	}

	private boolean isSource( TreeItem<Labeled> item )
	{
		if( agents.isEmpty() )
			return false;
		log.debug( "TreeView.getNodeLevel( item ): " + TreeView.getNodeLevel( item ) );
		log.debug( "getDepth(): " + getDepth() );
		return TreeView.getNodeLevel( item ) + 1 == getDepth();
	}

	public List<Selection> getSelections()
	{
		List<TreeItem<Labeled>> selectedItems = getSelectionModel().getSelectedItems();
		List<Selection> selections = new ArrayList<>( selectedItems.size() );
		for( int i = 0; i < selectedItems.size(); i++ )
		{
			TreeItem<Labeled> item = selectedItems.get( i );
			if( item.getChildren().isEmpty() ) // forbid selecting a parent (which is possible in multi-selections)
				selections.add( new Selection( item, isSource( item ) ) );
		}
		return selections;
	}

	private TreeItem<Labeled> treeItem( Labeled value, TreeItem<Labeled> parent )
	{
		TreeItem<Labeled> item = new TreeItem<>( value );
		parent.getChildren().add( item );
		return item;
	}

}
