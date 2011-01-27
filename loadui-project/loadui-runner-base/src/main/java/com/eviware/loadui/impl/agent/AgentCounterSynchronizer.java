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
package com.eviware.loadui.impl.agent;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eviware.loadui.api.counter.CounterHolder;
import com.eviware.loadui.api.counter.CounterSynchronizer;
import com.eviware.loadui.api.events.ActionEvent;
import com.eviware.loadui.api.events.BaseEvent;
import com.eviware.loadui.api.events.EventHandler;
import com.eviware.loadui.api.messaging.MessageEndpoint;
import com.eviware.loadui.api.model.ModelItem;

public class AgentCounterSynchronizer implements CounterSynchronizer
{
	private final static long DELAY = 1000;

	public final static Logger log = LoggerFactory.getLogger( AgentCounterSynchronizer.class );

	private final Timer timer = new Timer();
	private final BaseEventListener listener = new BaseEventListener();
	private final Map<CounterHolder, HolderData> holders = new HashMap<CounterHolder, HolderData>();

	public AgentCounterSynchronizer()
	{
		timer.schedule( new TimerTask()
		{
			@Override
			public void run()
			{
				Map<CounterHolder, HolderData> holdersCopy;
				synchronized( holders )
				{
					holdersCopy = new HashMap<CounterHolder, HolderData>( holders );
				}

				for( Entry<CounterHolder, HolderData> entry : holdersCopy.entrySet() )
				{
					CounterHolder holder = entry.getKey();
					Map<String, String> changedData = new HashMap<String, String>();
					Map<String, Long> counterData = entry.getValue().counterData;
					for( String counterName : holder.getCounterNames() )
					{
						long counterValue = holder.getCounter( counterName ).get();
						long oldValue = counterData.containsKey( counterName ) ? counterData.get( counterName ).longValue()
								: 0;

						if( oldValue != counterValue )
						{
							counterData.put( counterName, counterValue );
							changedData.put( counterName, Long.toString( counterValue ) );
						}
					}

					if( !changedData.isEmpty() )
					{
						changedData.put( COUNTER_HOLDER_ID, holder.getId() );
						entry.getValue().endpoint.sendMessage( CHANNEL, changedData );
					}
				}
			}
		}, DELAY, DELAY );
	}

	@Override
	public void syncCounters( CounterHolder counterHolder, MessageEndpoint endpoint )
	{
		synchronized( holders )
		{
			if( !holders.containsKey( counterHolder ) )
			{
				holders.put( counterHolder, new HolderData( endpoint ) );
				if( counterHolder instanceof ModelItem )
					( ( ModelItem )counterHolder ).addEventListener( BaseEvent.class, listener );
			}
		}
	}

	@Override
	public void unsyncCounters( CounterHolder counterHolder )
	{
		synchronized( holders )
		{
			if( holders.remove( counterHolder ) != null )
			{
				if( counterHolder instanceof ModelItem )
					( ( ModelItem )counterHolder ).removeEventListener( BaseEvent.class, listener );
			}
		}
	}

	@Override
	public void syncAggregator( String ownerId, Aggregator aggregator )
	{
		throw new UnsupportedOperationException( AgentCounterSynchronizer.class.getName()
				+ " does not support this method (syncAggregator)." );
	}

	@Override
	public void unsyncAggregator( String ownerId )
	{
	}

	private class BaseEventListener implements EventHandler<BaseEvent>
	{
		@Override
		public void handleEvent( BaseEvent event )
		{
			if( event instanceof ActionEvent && CounterHolder.COUNTER_RESET_ACTION.equals( event.getKey() ) )
				holders.get( event.getSource() ).counterData.clear();
			else if( ModelItem.RELEASED.equals( event.getKey() ) )
				unsyncCounters( ( CounterHolder )event.getSource() );
		}
	}

	private class HolderData
	{
		MessageEndpoint endpoint;
		Map<String, Long> counterData = new HashMap<String, Long>();

		public HolderData( MessageEndpoint endpoint )
		{
			this.endpoint = endpoint;
		}
	}
}