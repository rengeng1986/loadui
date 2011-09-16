/*
 * Copyright 2011 eviware software ab
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
package com.eviware.loadui.api.execution;

/**
 * Exception used when requesting a state change from an illegal state, for
 * instance, requesting a Start when already in a RUNNING state, or when a Start
 * has already been requested. Or, when requesting a Stop while already in an
 * IDLE state.
 * 
 * @author dain.nilsson
 */
@Deprecated
public final class IllegalLifecycleStateException extends Exception
{
	private static final long serialVersionUID = 152307637308761877L;

	public IllegalLifecycleStateException( String message )
	{
		super( message );
	}
}