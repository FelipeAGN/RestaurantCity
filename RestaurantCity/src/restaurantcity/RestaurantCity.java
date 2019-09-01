package restaurantcity;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class RestaurantCity extends Application
{    
    @Override
    public void start(Stage stage) throws Exception
    {
        Parent root=FXMLLoader.load(getClass().getResource("/ventanas/Login.fxml"));       
        Scene scene=new Scene(root);
        
        //stage.setWidth(1024.0);
        //stage.setHeight(650.0);
        stage.setResizable(false);
        stage.setTitle("Restaurant City");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args)
    {             
        launch(args);
    }
}
