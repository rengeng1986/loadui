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
package com.eviware.loadui.launcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;

import com.eviware.loadui.launcher.util.ErrorHandler;
import com.sun.javafx.PlatformUtil;

public class LauncherWatchdog implements Runnable
{
	private final static Logger log = Logger.getLogger( LauncherWatchdog.class.getName() );

	private final Framework framework;
	private final long timeout;

	public LauncherWatchdog( Framework framework, long timeout )
	{
		this.framework = framework;
		this.timeout = timeout;
	}

	@Override
	public void run()
	{
		try
		{
			Thread.sleep( timeout );
		}
		catch( InterruptedException e1 )
		{
			e1.printStackTrace();
		}

		for( Bundle bundle : framework.getBundleContext().getBundles() )
		{
			switch( bundle.getState() )
			{
			case Bundle.ACTIVE :
			case Bundle.RESOLVED :
				break;
			default :
				log.severe( String.format( "Bundle: %s failed state: %s", bundle.getSymbolicName(), bundle.getState() ) );
				log.severe( String.format( "Headers: %s", bundle.getHeaders() ) );

				try
				{
					bundle.start();
				}
				catch( BundleException e )
				{
					e.printStackTrace();
				}

				try
				{
					Thread.sleep( 5000 );
				}
				catch( InterruptedException e )
				{
					e.printStackTrace();
				}

				if( System.getProperty( "noclose" ) == null )
				{
					ErrorHandler.promptRestart();
				}
				break;
			}
		}
	}

}
