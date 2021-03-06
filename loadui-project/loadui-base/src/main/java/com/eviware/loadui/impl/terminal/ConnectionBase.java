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
package com.eviware.loadui.impl.terminal;

import com.eviware.loadui.api.terminal.Connection;
import com.eviware.loadui.api.terminal.InputTerminal;
import com.eviware.loadui.api.terminal.OutputTerminal;

public abstract class ConnectionBase implements Connection
{
	private final OutputTerminal output;
	private final InputTerminal input;

	public ConnectionBase( OutputTerminal output, InputTerminal input )
	{
		if( output == null || input == null )
			throw new IllegalArgumentException(
					"Cannot construct Connection if either InputTerminal or OutputTerminal is null!" );

		this.output = output;
		this.input = input;
	}

	@Override
	public InputTerminal getInputTerminal()
	{
		return input;
	}

	@Override
	public OutputTerminal getOutputTerminal()
	{
		return output;
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + "[" + output + ", " + input + "]";
	}
}
