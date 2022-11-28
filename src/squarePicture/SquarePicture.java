package squarePicture;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
/*
 * SquarePicture.java by Levi Johnston. Created between 11/9 and 11/12/2022
 * I wrote this program as a solution to a problem encountered in my eBusiness
 * class. On our eCommerce platform, product thumbnails are displayed as square
 * frames. However, when product images are rectangular in nature, key parts of
 * the image get cropped out. This program is designed to add gray space to fill
 * out the image so that it fits into a square frame without getting detail cropped
 * out. This program can handle multiple files input at once and allows the user
 * to add extra text to the file-name if desired. Input is taken from the user
 * via the mouse and keyboard. Files are accessed via the JFileChooser. Output
 * is displayed to the screen using a javafx GUI. File output is saved to the
 * user's device via java.io.
 */

public class SquarePicture extends Application{
	private Label completeLabel; // Shows status and gives messages to user
	static TextField fText; // The user can input additional file name characters here
	File[] selectedFile; // Array for the image files loaded by user
	static boolean changeName = false; // Boolean used for determining if user wants a new file name
	static ListView<String> newNames; // Displays a list of new file names
	static ListView<String> oldFLoc; // Shows a list of the original files in their current directory
	static Color color = Color.GRAY; // Used for changing fill color
	static String colorString;
	

	public static void main(String[] args) throws IOException
	{
		launch(args);
	}
	@Override
	public void start (Stage primaryStage)
	{
		// Set the window's title.
		primaryStage.setTitle("JPEG to Square JPEG");
	      
	    // Create Labels
	    Label greetingLabel = new Label("   JPEG to Square JPEG by Levi Johnston");
	    Label msgLabel = new Label("This program takes rectangular images and adds empty space around them, turning them into squares.");
	    Label NNLabel = new Label("New Name");
	    Label colorLabel = new Label("   Fill Color   ");
	    completeLabel = new Label("");
	    
	    // Set up Style for greetingLabel
	    greetingLabel.setId("greetingLabel");
	    greetingLabel.setMinHeight(100);
	    
	    // Align msgLabel
	    msgLabel.setId("msgLabel");
	    msgLabel.setMaxWidth(700);
	    msgLabel.setMinHeight(70);
	    msgLabel.setWrapText(true);
	    
	    	    
	    // Load image from file
      	Image image = new Image("file:logo.png");
	      
      	// Create an ImageView control.
      	ImageView imageView = new ImageView(image);
      	imageView.setFitHeight(100);
      	imageView.setFitWidth(100);
	    
	    // Create Text Fields
	    fText = new TextField();
	    
	    // Create Checkbox and set up actions for when it is pressed
	    // This Checkbox is used to tell if the user wants new file names or not
	    CheckBox editFName = new CheckBox("Adjust Filename(s)   ");
	    editFName.setOnAction(event ->
	    {
	    	if (editFName.isSelected())
	    	{
	    		changeName = true;
	    	}
	    	else
	    	{
	    		changeName = false;
	    	}
	    	refreshNewNames(selectedFile);
	    });
	    
	    // ListViews for displaying file information
	    newNames = new ListView<String>();
	    oldFLoc = new ListView<String>();
	    
	    
	    // Create a ComboBox
	    ComboBox<String> colorPicker = new ComboBox<>();
	    colorPicker.setEditable(true);
	    colorPicker.getItems().addAll("Black", "Blue", "Green", "Yellow", "Red", "Orange", "Pink", "White", "Gray");
	    colorPicker.setOnAction(event ->
	    {
	    	// When the user selects a color, we will grab the string, convert it to upper, and use that value to
	    	// get a value from the class Color. We will later use that value when creating our image.
	    	try
	    	{
	    		colorString = colorPicker.getValue().toUpperCase();
	    		// I don't know what reflection is, but it's how I made this section of code work
	    		Field field = Class.forName("java.awt.Color").getField(colorString);
	    	    color = (Color)field.get(null);
	    	}
	    	// Default back to gray if there is an error
	    	catch (Exception e)
	    	{
	    		color = Color.GRAY;
	    		completeLabel.setText("Invalid Color. Select from the list or try typing one in manually to see if it is available.");
	    	}
	    });
	    
	    // Create Buttons
	    Button addFile = new Button("Add file(s)");
	    Button confirm = new Button("Convert image(s)");
	    
	    // Create HBoxs
	    HBox hbox = new HBox(editFName, fText, colorLabel, colorPicker);
	    HBox hbox2 = new HBox(imageView, greetingLabel);
	    
	    // Create VBox
	    VBox vbox = new VBox(hbox2, msgLabel, addFile, hbox, NNLabel, newNames, confirm, completeLabel, oldFLoc);
	    vbox.setPadding(new Insets(10));
	    
	    // Set the margin for each element in vbox
	    for (int i = 0; i < vbox.getChildren().size(); i++)
	    {
	    	VBox.setMargin(vbox.getChildren().get(i), new Insets(5));
	    }
	    
	    // Set up event listeners for button clicks
	    addFile.setOnAction(new AFClickHandler());
	    confirm.setOnAction(new ConfirmClickHandler());
	    
	    Scene scene = new Scene (vbox, 800, 600);
	    scene.getStylesheets().add("Styles.css");
	    primaryStage.setScene(scene);
	    
	    primaryStage.show();
	    
	    
	}
	
	// This method returns whether the selected files have the extension .jpg or .jpeg
	public static boolean validFiles(File[] file)
	{
		for (File f: file)
		{
			if ((!(f.toString().substring(f.toString().lastIndexOf('.'))).equalsIgnoreCase(".jpg")) && (!(f.toString().substring(f.toString().lastIndexOf('.'))).equalsIgnoreCase(".jpeg")))
			{
				return false;
			}
		}
		return true;
	}
	
	// This method refreshes the new file names displayed in the ListView
	public static void refreshNewNames(File[] f)
	{
		newNames.getItems().clear();
		
			for (File file: f)
			{
				String fileName = new String(file.getName().substring(0, file.getName().lastIndexOf(".")) + fText.getText() + ".jpg");
				if (changeName)
				{
					newNames.getItems().add(fileName);
				}
				else
				{
					newNames.getItems().add(file.getName());
				}
			}
	}
	
	// Class for handling the addFile button
	class AFClickHandler implements EventHandler<ActionEvent>
	{
		@Override
		public void handle(ActionEvent event)
		{
			// Create a new instance of the FileChooser class
			JFileChooser fileChooser = new JFileChooser();
			
			// Allow for multiple files to be selected and set home directory
			fileChooser.setMultiSelectionEnabled(true);
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
			int result = fileChooser.showOpenDialog(null);
			
			// If the user selects one or more files and selects open
			if (result == JFileChooser.APPROVE_OPTION) {
				
				// Create an array of files and match them to the files specified by the user
				selectedFile = fileChooser.getSelectedFiles();
				
				// Refresh the ListView
				refreshNewNames(selectedFile);
				
			    // Add each file name to fileNames and display
			    for (File f: selectedFile )
			    {
			    	oldFLoc.getItems().add(f.getAbsolutePath());
			    }
			    
			    // Display a message if the files aren't valid
			    if (!validFiles(selectedFile))
			    {
			    	completeLabel.setText("Error: One or more of the selected files are not of the type .jpg or .jpeg");

			    }
			}
		}
	}
	
	// Class for handling the confirm button
		class ConfirmClickHandler implements EventHandler<ActionEvent>
		{
			@Override
			public void handle(ActionEvent event)
			{
				editImage(selectedFile, fText.getText());
			}
			
			// This method reads the rgb information from each of the selected files
			// and writes data to a new array of files accordingly
			private void editImage(File[] file, String ext)
			{
				// Declare variables for width and height of loaded image
				int w, h;
				// Just in case the file is incorrectly labeled .jpg or is corrupt
				try {
					if (validFiles(file))
				    {
						for (int index = 0; index < file.length; index++)
						{
							// Read the file as a buffered image and get the width and height
							BufferedImage img = ImageIO.read(file[index]);
							w = img.getWidth();
							h = img.getHeight();
							
							
							// Horizontal images
							 if (w > h)
							 {
								 // Create an instance of BufferedImage to copy my picture to
								 BufferedImage newImg = new BufferedImage(w, w, BufferedImage.TYPE_INT_RGB);
								 
								 // Paint the top pixels our color of choice
								 for (int j = 0; j < (w - h) / 2; j++)
								 {
									 for ( int i = 0; i < w; i++)
									 {
										 newImg.setRGB(i, j, color.getRGB());
									 }
								 }
								 
								 // Copy our image to the middle
								 for (int j = 0; j < h; j++)
								 {
									 for (int i = 0; i < w; i++)
									 {
										 newImg.setRGB(i, j + ((w - h) / 2), img.getRGB(i, j));
									 }
								 }
								 
								 // Paint the bottom pixels our color of choice
								 for (int j = ((w - h)/ 2) + h; j < w; j++)
								 {
									 for (int i = 0; i < w; i++)
									 {
										 newImg.setRGB(i, j, color.getRGB());
									 }
								 }
								 // Write the image to a new file with the type jpg
								 File outputFile = new File(newNames.getItems().get(index));
								 ImageIO.write(newImg, "jpg", outputFile);
							 }
							 
							 // Vertical Images
							 else if (h > w)
							 {
								 // Create an instance of BufferedImage to copy my picture to
								 BufferedImage newImg = new BufferedImage(h, h, BufferedImage.TYPE_INT_RGB);
								 
								 // Paint the left pixels our color of choice
								 for (int j = 0; j < (h - w) / 2; j++)
								 {
									 for ( int i = 0; i < h; i++)
									 {
										 newImg.setRGB(j, i, color.getRGB());
									 }
								 }
								 
								 // Copy our image to the middle
								 for (int j = 0; j < w; j++)
								 {
									 for (int i = 0; i < h; i++)
									 {
										 newImg.setRGB(j + ((h - w) / 2), i, img.getRGB(j, i));
									 }
								 }
								 
								 // Paint the right pixels our color of choice
								 for (int j = ((h - w)/ 2) + w; j < h; j++)
								 {
									 for (int i = 0; i < h; i++)
									 {
										 newImg.setRGB(j, i, color.getRGB());
									 }
								 }
								 
								 // Write the image to a new file with the type jpg
								 File outputFile = new File(newNames.getItems().get(index));
								 ImageIO.write(newImg, "jpg", outputFile);
							 }
						}
					}
					// Tell user operation is complete
					completeLabel.setText("Complete!");
				}
				catch (Exception e)
				{
					completeLabel.setText("Error: One or more of the selected files are not of the type .jpg or .jpeg");
				}
				
			}
		}
}
