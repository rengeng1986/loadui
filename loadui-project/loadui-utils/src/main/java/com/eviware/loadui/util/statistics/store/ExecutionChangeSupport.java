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
package com.eviware.loadui.util.statistics.store;

import java.util.ArrayList;
import java.util.List;

import com.eviware.loadui.api.statistics.store.ExecutionListener;
import com.eviware.loadui.api.statistics.store.ExecutionManager;
import com.eviware.loadui.api.statistics.store.TrackDescriptor;

public class ExecutionChangeSupport
{
	private final List<ExecutionListener> listeners = new ArrayList<>();

	public void addExecutionListener( ExecutionListener el )
	{
		listeners.add( el );
	}

	public void fireExecutionPaused( ExecutionManager.State oldState )
	{
		for( ExecutionListener el : new ArrayList<>( listeners ) )
			el.executionPaused( oldState );
	}

	public void fireExecutionStarted( ExecutionManager.State oldState )
	{
		for( ExecutionListener el : new ArrayList<>( listeners ) )
			el.executionStarted( oldState );
	}

	public void fireExecutionStopped( ExecutionManager.State oldState )
	{
		for( ExecutionListener el : new ArrayList<>( listeners ) )
			el.executionStopped( oldState );
	}

	public void fireTrackRegistered( TrackDescriptor trackDescriptor )
	{
		for( ExecutionListener el : new ArrayList<>( listeners ) )
			el.trackRegistered( trackDescriptor );
	}

	public void fireTrackUnregistered( TrackDescriptor trackDescriptor )
	{
		for( ExecutionListener el : new ArrayList<>( listeners ) )
			el.trackUnregistered( trackDescriptor );
	}

	public void removeAllExecutionListeners()
	{
		listeners.clear();
	}

	public void removeExecutionListener( ExecutionListener el )
	{
		listeners.remove( el );
	}
}
