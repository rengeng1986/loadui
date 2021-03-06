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
package com.eviware.loadui.util.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.EventObject;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.eviware.loadui.api.events.BaseEvent;
import com.eviware.loadui.api.events.EventFirer;
import com.eviware.loadui.api.events.EventHandler;
import com.eviware.loadui.util.events.EventSupport;
import com.google.common.base.Objects;

public class TestUtilsTest
{
	@Test
	public void shouldAwaitEventsPreviouslyFired() throws InterruptedException, ExecutionException, TimeoutException
	{
		EventFirer eventFirer = new EventFirer()
		{
			private final EventSupport eventSupport = new EventSupport( this );

			@Override
			public <T extends EventObject> void removeEventListener( Class<T> type, EventHandler<? super T> listener )
			{
				eventSupport.removeEventListener( type, listener );
			}

			@Override
			public void fireEvent( EventObject event )
			{
				eventSupport.fireEvent( event );
			}

			@Override
			public void clearEventListeners()
			{
				eventSupport.clearEventListeners();
			}

			@Override
			public <T extends EventObject> void addEventListener( Class<T> type, EventHandler<? super T> listener )
			{
				eventSupport.addEventListener( type, listener );
			}
		};

		final AtomicInteger eventCount = new AtomicInteger();

		eventFirer.addEventListener( BaseEvent.class, new EventHandler<BaseEvent>()
		{
			@Override
			public void handleEvent( BaseEvent event )
			{
				if( !Objects.equal( event.getKey(), "increment" ) )
				{
					return;
				}

				try
				{
					Thread.sleep( 5 );
				}
				catch( InterruptedException e )
				{
					e.printStackTrace();
					Thread.currentThread().interrupt();
				}
				finally
				{
					eventCount.incrementAndGet();
				}
			}
		} );

		eventFirer.fireEvent( new BaseEvent( eventFirer, "increment" ) );
		eventFirer.fireEvent( new BaseEvent( eventFirer, "increment" ) );

		TestUtils.awaitEvents( eventFirer );
		assertThat( eventCount.get(), is( 2 ) );
	}
}
