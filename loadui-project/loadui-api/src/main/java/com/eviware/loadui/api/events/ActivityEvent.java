/* 
 * Copyright 2011 SmartBear Software
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
package com.eviware.loadui.api.events;

import java.util.EventObject;

import com.eviware.loadui.api.component.ActivityStrategy;

public class ActivityEvent extends EventObject
{
	private static final long serialVersionUID = -3481011357076103726L;

	public ActivityEvent( ActivityStrategy source )
	{
		super( source );
	}

	public boolean isActive()
	{
		return ( ( ActivityStrategy )getSource() ).isActive();
	}
}
