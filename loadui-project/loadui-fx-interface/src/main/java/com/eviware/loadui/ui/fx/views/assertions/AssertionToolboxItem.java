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
package com.eviware.loadui.ui.fx.views.assertions;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eviware.loadui.api.assertion.AssertionItem;
import com.eviware.loadui.ui.fx.control.DragNode;
import com.eviware.loadui.ui.fx.util.Properties;
import com.eviware.loadui.ui.fx.util.UIUtils;
import com.eviware.loadui.ui.fx.views.canvas.CanvasView;

public class AssertionToolboxItem extends Label
{
	protected static final Logger log = LoggerFactory.getLogger( AssertionToolboxItem.class );

	private final AssertionItem<?> assertion;

	public AssertionToolboxItem( final AssertionItem<?> assertion )
	{
		this.assertion = assertion;

		getStyleClass().add( "icon" );

		setMaxHeight( 80 );
		setMinHeight( 80 );

		textProperty().bind( Properties.forLabel( assertion ) );

		final ImageView icon;
		Image image = UIUtils.getImageFor( assertion );
		if( image == null )
			log.debug( "No image found for holder " + assertion );
		icon = new ImageView( image );

		DragNode dragNode = DragNode.install( AssertionToolboxItem.this, new ImageView( icon.getImage() ) );
		dragNode.setData( assertion );

		setGraphic( icon );
	}

	public AssertionItem<?> getHolder()
	{
		return assertion;
	}

	@Override
	public String toString()
	{
		return assertion.getLabel();
	}
}
