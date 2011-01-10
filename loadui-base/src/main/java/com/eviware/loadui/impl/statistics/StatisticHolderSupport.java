/*
 * Copyright 2010 eviware software ab
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the Licence for the specific language governing permissions and limitations
 * under the Licence.
 */
package com.eviware.loadui.impl.statistics;

import java.util.Collections;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eviware.loadui.api.events.CollectionEvent;
import com.eviware.loadui.api.statistics.MutableStatisticVariable;
import com.eviware.loadui.api.statistics.StatisticHolder;
import com.eviware.loadui.api.statistics.StatisticVariable;
import com.eviware.loadui.api.statistics.StatisticsManager;
import com.eviware.loadui.api.statistics.StatisticsWriter;
import com.eviware.loadui.util.BeanInjector;


/**
 * Support class for Objects implementing StatisticHolder. Handles creating and
 * listing StatisticVariables, firing events, instantiating StatisticsWriters,
 * etc.
 * 
 * @author dain.nilsson
 */
public class StatisticHolderSupport
{
	public static Logger log = LoggerFactory.getLogger( StatisticHolderSupport.class );
	
	private final StatisticsManager manager;
	private final StatisticHolder owner;
	private final HashMap<String, StatisticVariableImpl> variables = new HashMap<String, StatisticVariableImpl>();

	public StatisticHolderSupport( StatisticHolder owner )
	{
		this.owner = owner;
		manager = BeanInjector.getBean( StatisticsManager.class );
	}

	/**
	 * Initialized the StatisticHolderSupport and registers the StatisticHolder.
	 * Should be called once the StatisticHolder has been fully initialized.
	 */
	public void init()
	{
		manager.registerStatisticHolder( owner );
	}

	/**
	 * Instantiates a StatisticsWriter for the given type, and adds it to the
	 * given StatisticVariable.
	 * 
	 * @param type
	 * @param variable
	 * @return
	 */
	public void addStatisticsWriter( String type, StatisticVariable variable )
	{
		if( !variables.containsValue( variable ) )
			throw new IllegalArgumentException(
					"Attempt made to add a StatisticsWriter for a StatisticVariable not contained in the parent StatisticHolder." );

		// TODO: Fire CollectionEvent about Statistics exposed by the
		// StatisticsWriter.
		StatisticsWriter writer = StatisticsManagerImpl.getInstance().createStatisticsWriter( type, variable );
		( ( StatisticVariableImpl )variable ).addStatisticsWriter( writer );
	}

	/**
	 * Adds a StatisticVariable to the StatisticHolder, and returns it.
	 * 
	 * @param statisticVariableName
	 * @return
	 */
	public MutableStatisticVariable addStatisticVariable( String statisticVariableName )
	{
		if( variables.containsKey( statisticVariableName ) )
			return variables.get( statisticVariableName );

		StatisticVariableImpl variable = new StatisticVariableImpl( manager.getExecutionManager(), owner,
				statisticVariableName );
		variables.put( statisticVariableName, variable );
		owner.fireEvent( new CollectionEvent( owner, StatisticHolder.STATISTICS, CollectionEvent.Event.ADDED, variable ) );

		return variable;
	}
	
	/**
	 * Removes a StatisticVariable from the StatisticHolder.
	 * 
	 * @param statisticVariableName
	 */
	public void removeStatisticalVariable( String statisticVariableName )
	{
		if( !variables.containsKey( statisticVariableName ) )
			throw new NoSuchElementException
				("Attempt made to remove a non-existing StatisticVarible from a StatisticHolder.");
		
		StatisticVariableImpl removedVariable = variables.remove( statisticVariableName );
		owner.fireEvent( new CollectionEvent( owner, StatisticHolder.STATISTICS, CollectionEvent.Event.REMOVED, removedVariable) );
		log.debug( "Fired CollectionEvent: removed statistic variable!" );
	}

	public StatisticVariable getStatisticVariable( String statisticVariableName )
	{
		return variables.get( statisticVariableName );
	}

	public Set<String> getStatisticVariableNames()
	{
		return Collections.unmodifiableSet( variables.keySet() );
	}

	/**
	 * Removes any StatisticVariables and Statistics for the StatisticHolder, and
	 * removes the StatisticHolder from the registry. Should be called when the
	 * StatisticHolder is destroyed.
	 */
	public void release()
	{
		manager.deregisterStatisticHolder( owner );
	}
	
}