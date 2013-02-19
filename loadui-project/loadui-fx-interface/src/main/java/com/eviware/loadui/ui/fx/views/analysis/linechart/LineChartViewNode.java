package com.eviware.loadui.ui.fx.views.analysis.linechart;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eviware.loadui.api.charting.line.ZoomLevel;
import com.eviware.loadui.api.execution.Phase;
import com.eviware.loadui.api.execution.TestExecution;
import com.eviware.loadui.api.execution.TestExecutionTask;
import com.eviware.loadui.api.execution.TestRunner;
import com.eviware.loadui.api.statistics.model.chart.line.LineChartView;
import com.eviware.loadui.api.statistics.store.Execution;
import com.eviware.loadui.ui.fx.api.NonSingletonFactory;
import com.eviware.loadui.ui.fx.api.analysis.ExecutionChart;
import com.eviware.loadui.ui.fx.util.DefaultNonSingletonFactory;
import com.eviware.loadui.ui.fx.util.FXMLUtils;
import com.eviware.loadui.ui.fx.util.Properties;
import com.eviware.loadui.util.BeanInjector;
import com.eviware.loadui.util.execution.TestExecutionUtils;

public class LineChartViewNode extends VBox
{
	public static final String POSITION_ATTRIBUTE = "position";
	public static final String TIME_SPAN_ATTRIBUTE = "timeSpan";
	public static final String ZOOM_LEVEL_ATTRIBUTE = "zoomLevel";
	public static final String FOLLOW_ATTRIBUTE = "follow";

	protected static final Logger log = LoggerFactory.getLogger( LineChartViewNode.class );

	private final TestExecutionTask executionTask = new TestExecutionTask()
	{
		@Override
		public void invoke( TestExecution execution, Phase phase )
		{
			if( phase == Phase.START )
			{
				followCheckBox.setDisable( false );
			}
			else if( phase == Phase.STOP )
			{
				followCheckBox.setDisable( true );
			}
		}
	};

	private final ObservableValue<Execution> currentExecution;
	private final Observable poll;
	private final LineChartView chartView;

	private ExecutionChart executionChart;

	@FXML
	private StackPane chartContainer;

	@FXML
	private ZoomMenuButton zoomMenuButton;

	@FXML
	private ToggleButton followCheckBox;

	public LineChartViewNode( final ObservableValue<Execution> currentExecution, LineChartView chartView, Observable poll )
	{
		log.debug( "new LineChartViewNode created! " );

		NonSingletonFactory nonSingletonFactory = getNonSingletonFactory();
		executionChart = nonSingletonFactory.createExecutionChart();

		BeanInjector.getBean( TestRunner.class ).registerTask( executionTask, Phase.START, Phase.STOP );

		this.currentExecution = currentExecution;
		this.chartView = chartView;
		this.poll = poll;

		FXMLUtils.load( this );
	}

	protected NonSingletonFactory getNonSingletonFactory()
	{
		NonSingletonFactory factory = BeanInjector.getNonCachedBeanOrNull( NonSingletonFactory.class );
		if( factory != null )
			return factory;
		else
			return DefaultNonSingletonFactory.get();
	}

	@FXML
	public void initialize()
	{
		log.debug( "INITIALIZE LineChartViewNode STARTED" );

		loadAttributes();

		chartContainer.getChildren().add( executionChart.getNode() );

		executionChart.setChartProperties( currentExecution, chartView, poll );

		executionChart.titleProperty().bind( Properties.forLabel( chartView ) );

		zoomMenuButton.selectedProperty().addListener( new ChangeListener<ZoomLevel>()
		{
			@Override
			public void changed( ObservableValue<? extends ZoomLevel> arg0, ZoomLevel arg1, ZoomLevel newZoomLevel )
			{
				setZoomLevel( newZoomLevel );
			}
		} );

		currentExecution.addListener( new InvalidationListener()
		{
			@Override
			public void invalidated( Observable _ )
			{
				//sets the position to 0 when there is a new execution
				executionChart.setPosition( 0d );
			}
		} );
		followCheckBox.setDisable( !TestExecutionUtils.isExecutionRunning() );
		followCheckBox.selectedProperty().bindBidirectional( executionChart.scrollbarFollowStateProperty() );

	}

	private void loadAttributes()
	{
		ZoomLevel level;
		try
		{
			level = ZoomLevel.valueOf( chartView.getAttribute( ZOOM_LEVEL_ATTRIBUTE, "SECONDS" ) );
		}
		catch( IllegalArgumentException e )
		{
			level = ZoomLevel.SECONDS;
		}
		zoomMenuButton.setSelected( level );

		log.debug( "Zoomlevel: " + level.toString() );

		Boolean follow;
		try
		{
			follow = Boolean.parseBoolean( chartView.getAttribute( FOLLOW_ATTRIBUTE, "true" ) );
		}
		catch( IllegalArgumentException e )
		{
			follow = true;
		}
		followCheckBox.setSelected( follow );

	}

	public void setZoomLevel( ZoomLevel zoomLevel )
	{
		executionChart.setZoomLevel( zoomLevel );
		chartView.setAttribute( ZOOM_LEVEL_ATTRIBUTE, zoomLevel.name() );

	}

	public LineChart<Number, Number> getLineChart()
	{
		return executionChart.getLineChart();
	}

}
