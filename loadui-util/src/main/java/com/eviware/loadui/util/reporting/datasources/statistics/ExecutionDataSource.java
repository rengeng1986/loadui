package com.eviware.loadui.util.reporting.datasources.statistics;

import java.awt.Image;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import com.eviware.loadui.LoadUI;
import com.eviware.loadui.api.statistics.model.StatisticPage;
import com.eviware.loadui.api.statistics.store.Execution;
import com.eviware.loadui.util.FormattingUtils;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.data.JRAbstractBeanDataSource;

public class ExecutionDataSource extends JRAbstractBeanDataSource
{
	private final Date date = new Date();
	private final String label;
	private final Execution execution;
	private final Collection<StatisticPage> pages;
	private final Map<Object, Image> charts;
	private StatisticPage page;
	private Iterator<StatisticPage> iterator;

	public ExecutionDataSource( String label, Execution execution, Collection<StatisticPage> pages,
			Map<Object, Image> charts )
	{
		super( true );

		this.label = label;
		this.execution = execution;
		this.pages = pages;
		this.charts = charts;
		iterator = pages.iterator();
	}

	@Override
	public void moveFirst() throws JRException
	{
		iterator = pages.iterator();
	}

	@Override
	public Object getFieldValue( JRField field ) throws JRException
	{
		String fieldName = field.getName();
		if( fieldName.equals( "loaduiVersion" ) )
			return LoadUI.VERSION;
		if( fieldName.equals( "currentTime" ) )
			return date.toString();
		if( fieldName.equals( "projectName" ) )
			return label;
		if( fieldName.equals( "pageName" ) )
			return page.getTitle();
		if( fieldName.equals( "runtime" ) )
			return FormattingUtils.formatTime( execution.getLength() / 1000 );
		if( fieldName.equals( "chartGroup" ) )
			return new ChartGroupsDataSource( page.getChildren(), charts );
		return fieldName;
	}

	@Override
	public boolean next() throws JRException
	{
		if( iterator.hasNext() )
		{
			page = iterator.next();
			return true;
		}
		return false;
	}
}