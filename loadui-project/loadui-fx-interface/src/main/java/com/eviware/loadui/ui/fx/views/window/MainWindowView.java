package com.eviware.loadui.ui.fx.views.window;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import com.eviware.loadui.api.events.BaseEvent;
import com.eviware.loadui.api.events.WeakEventHandler;
import com.eviware.loadui.api.model.ProjectItem;
import com.eviware.loadui.api.model.ProjectRef;
import com.eviware.loadui.api.model.WorkspaceItem;
import com.eviware.loadui.api.model.WorkspaceProvider;
import com.eviware.loadui.api.traits.Labeled;
import com.eviware.loadui.ui.fx.api.input.Selectable;
import com.eviware.loadui.ui.fx.api.intent.IntentEvent;
import com.eviware.loadui.ui.fx.util.FXMLUtils;
import com.eviware.loadui.ui.fx.util.UIUtils;
import com.eviware.loadui.ui.fx.views.about.AboutDialog;
import com.eviware.loadui.ui.fx.views.project.ProjectView;
import com.eviware.loadui.ui.fx.views.project.SaveProjectDialog;
import com.eviware.loadui.ui.fx.views.rename.RenameDialog;
import com.eviware.loadui.ui.fx.views.workspace.GlobalSettingsDialog;
import com.eviware.loadui.ui.fx.views.workspace.SystemPropertiesDialog;
import com.eviware.loadui.ui.fx.views.workspace.WorkspaceView;
import com.google.common.base.Preconditions;

public class MainWindowView extends StackPane
{
	@FXML
	private MenuButton mainButton;

	@FXML
	private StackPane container;

	private final WorkspaceProvider workspaceProvider;
	private final Property<WorkspaceItem> workspaceProperty = new SimpleObjectProperty<>();
	private final WorkspaceListener workspaceListener = new WorkspaceListener();

	public MainWindowView( WorkspaceProvider workspaceProvider )
	{
		this.workspaceProvider = Preconditions.checkNotNull( workspaceProvider );

		FXMLUtils.load( this );
	}

	@FXML
	private void initialize()
	{
		workspaceProvider.addEventListener( BaseEvent.class, workspaceListener );

		if( workspaceProvider.isWorkspaceLoaded() )
		{
			workspaceProperty.setValue( workspaceProvider.getWorkspace() );
		}
		else
		{
			workspaceProperty.setValue( workspaceProvider.loadDefaultWorkspace() );
		}

		try
		{
			mainButton.setGraphic( new ImageView( "res/logo-button.png" ) );
		}
		catch( Exception e1 )
		{
			//e1.printStackTrace();
		}

		Selectable.installDeleteKeyHandler( this );

		addEventHandler( IntentEvent.ANY, new EventHandler<IntentEvent<? extends Object>>()
		{
			@Override
			public void handle( IntentEvent<? extends Object> event )
			{
				if( event.isConsumed() )
				{
					return;
				}

				if( event.getEventType() == IntentEvent.INTENT_OPEN )
				{
					if( event.getArg() instanceof ProjectRef )
					{
						final ProjectRef projectRef = ( ProjectRef )event.getArg();
						Task<Void> openProject = new Task<Void>()
						{
							@Override
							protected Void call() throws Exception
							{
								updateMessage( "Loading project: " + projectRef.getLabel() );
								try
								{
									projectRef.setEnabled( true );
									final ProjectItem project = projectRef.getProject();
									Platform.runLater( new Runnable()
									{
										@Override
										public void run()
										{
											container.getChildren().setAll( new ProjectView( project ) );
										}
									} );
								}
								catch( Exception e )
								{
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								return null;
							}
						};

						fireEvent( IntentEvent.create( IntentEvent.INTENT_RUN_BLOCKING, openProject ) );

						//new Thread( openProject ).start();
					}
					else
					{
						System.out.println( "Unhandled intent: " + event );
						return;
					}
				}
				else if( event.getEventType() == IntentEvent.INTENT_CLOSE )
				{
					if( event.getArg() instanceof ProjectItem )
					{
						final ProjectItem project = ( ProjectItem )event.getArg();
						if( project.isDirty() )
						{
							SaveProjectDialog saveDialog = new SaveProjectDialog( MainWindowView.this, project );
							saveDialog.show();
						}
						else
						{
							showWorkspace();
							project.release();
						}
						//TODO: Need to have the ProjectRef close the project.
					}
					else
					{
						System.out.println( "Unhandled intent: " + event );
						return;
					}
				}
				else if( event.getEventType() == IntentEvent.INTENT_RENAME )
				{
					final Object arg = event.getArg();
					Preconditions.checkArgument( arg instanceof Labeled.Mutable );
					new RenameDialog( ( Labeled.Mutable )arg, MainWindowView.this ).show();
				}
				else if( event.getEventType() == IntentEvent.INTENT_RUN_BLOCKING )
				{
					//Handled by BlockingTask.
					return;
				}
				else
				{
					System.out.println( "Unhandled intent: " + event );
					return;
				}
				event.consume();
			}
		} );
		showWorkspace();

	}

	public void showWorkspace()
	{
		container.getChildren().setAll( new WorkspaceView( workspaceProvider.getWorkspace() ) );
	}

	public void settings()
	{
		new GlobalSettingsDialog( mainButton, workspaceProperty.getValue() ).show();
	}

	public void systemProperties()
	{
		fireEvent( IntentEvent.create( IntentEvent.INTENT_RUN_BLOCKING, new Runnable()
		{
			@Override
			public void run()
			{
				SystemPropertiesDialog.initialize();

				Platform.runLater( new Runnable()
				{
					@Override
					public void run()
					{
						new SystemPropertiesDialog( mainButton ).show();
					}
				} );
			}
		} ) );
	}

	public void feedback()
	{
		UIUtils.openInExternalBrowser( "http://www.eviware.com/forum/viewforum.php?f=9" );
	}

	public void about()
	{
		new AboutDialog( mainButton ).show( getScene().getWindow() );
	}

	private class WorkspaceListener implements WeakEventHandler<BaseEvent>
	{
		@Override
		public void handleEvent( BaseEvent event )
		{
			if( WorkspaceProvider.WORKSPACE_LOADED.equals( event.getKey() ) )
			{
				workspaceProperty.setValue( workspaceProvider.getWorkspace() );
			}
		}
	}
}
