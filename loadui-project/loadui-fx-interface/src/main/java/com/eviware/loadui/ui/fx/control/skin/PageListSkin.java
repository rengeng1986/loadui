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
package com.eviware.loadui.ui.fx.control.skin;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.StackPaneBuilder;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.util.Callback;

import com.eviware.loadui.ui.fx.control.PageList;
import com.eviware.loadui.ui.fx.control.ScrollableList;
import com.eviware.loadui.ui.fx.util.ObservableLists;
import com.google.common.base.Function;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.skin.SkinBase;

public class PageListSkin<E extends Node> extends SkinBase<PageList<E>, BehaviorBase<PageList<E>>>
{
	private final ScrollableList<E> itemList = new ScrollableList<>();
	private final ScrollableList<Label> labelList = new ScrollableList<>();

	private final ObservableList<Label> labels;

	public PageListSkin( PageList<E> pageList )
	{
		super( pageList, new BehaviorBase<>( pageList ) );

		Bindings.bindContent( itemList.getItems(), pageList.getItems() );

		labelList.getStyleClass().add( "label-list" );

		itemList.setOrientation( Orientation.HORIZONTAL );
		labelList.setOrientation( Orientation.HORIZONTAL );
		itemList.spacingProperty().bind( pageList.spacingProperty() );
		labelList.spacingProperty().bind( pageList.spacingProperty() );
		itemList.sizePerItemProperty().bind( pageList.widthPerItemProperty() );
		labelList.sizePerItemProperty().bind( pageList.widthPerItemProperty() );
		itemList.placeholderTextProperty().bind( pageList.placeholderTextProperty() );
		VBox.setVgrow( itemList, Priority.ALWAYS );

		labelList.pageProperty().bindBidirectional( itemList.pageProperty() );

		Label label = pageList.getLabel();
		StackPane.setAlignment( label, Pos.TOP_LEFT );

		Label pageNum = new Label();
		pageNum.getStyleClass().add( "page-number" );
		pageNum.textProperty().bind(
				Bindings.format( "Page %d of %d", itemList.pageProperty().add( 1 ), itemList.numPagesProperty() ) );
		StackPane.setAlignment( pageNum, Pos.TOP_RIGHT );

		labels = ObservableLists.transform( itemList.getItems(), new Function<E, Label>()
		{
			@Override
			public Label apply( E input )
			{
				Callback<? super E, ? extends Label> labelFactory = getSkinnable().getLabelFactory();
				return labelFactory != null ? labelFactory.call( input ) : new Label( input.toString() );
			}
		} );
		Bindings.bindContent( labelList.getItems(), labels );

		VBox vbox = VBoxBuilder
				.create()
				.fillWidth( true )
				.children( StackPaneBuilder.create().styleClass( "label-container" ).children( label, pageNum ).build(),
						itemList, new Separator(), labelList ).build();
		getChildren().setAll( vbox );
	}
}
