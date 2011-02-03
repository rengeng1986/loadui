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
package com.eviware.loadui.util;

import java.util.Collection;

import com.eviware.loadui.api.model.Releasable;

/**
 * Utility class for handling Releasables.
 * 
 * @author dain.nilsson
 */
public class ReleasableUtils
{
	/**
	 * Releases an Object if it is Releasable, otherwise does nothing.
	 * 
	 * @param object
	 */
	public static void release( Object object )
	{
		if( object instanceof Releasable )
			( ( Releasable )object ).release();
	}

	/**
	 * Releases all Releasable objects in the given varargs. For arguments that
	 * are a Collection, each of its children will be released.
	 * 
	 * @param objects
	 */
	public static void releaseAll( Object... objects )
	{
		for( Object object : objects )
		{
			if( object instanceof Collection )
			{
				for( Object child : ( Collection<?> )object )
					release( child );
			}
			release( object );
		}
	}
}