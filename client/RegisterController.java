package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Window;

public class RegisterController  {
    private RootController rootController;



    @FXML
    private TextField nameField;
    @FXML
    private Button submitButton;



//    @Override
//    public void start (Stage primaryStage) throws IOException{
//        Parent root = FXMLLoader.load(getClass().getResource("register.fxml"));
//        primaryStage.setTitle("Registration Form ");
//       primaryStage.setScene(new Scene(root, 600,400));
//        handleSubmitButtonAction();
//        primaryStage.show();
//
//    }

    @FXML
    protected void handleSubmitButtonAction(ActionEvent event) {
        Window owner = submitButton.getScene().getWindow();
        if(nameField.getText().isEmpty()){
            AlertHelper(Alert.AlertType.ERROR, owner, "Form error","Name is required");
            return;
        }
        if(!nameField.getText().matches("[0-9]")){
            AlertHelper(Alert.AlertType.ERROR, owner, "Form error","NameField is require");
            return;
        }


    }
    private void AlertHelper(Alert.AlertType alertType, Window owner, String title, String message){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();

    }
//    public static void main(String[] args) {
//        launch(args);
//    }
    public void setRootController(RootController rootController) {
        this.rootController = rootController;
    }



//
//    @Override
//    public void handle(KeyEvent keyEvent) {
//        TextField nField = new TextField();
//        TextFormatter<?> formatter = new TextFormatter<>(change -> {
//            String newText = change.getControlNewText();
//            return newText.matches("^(?=[a-zA-Z0-9._]{8,20}$)(?!.*[_.]{2})[^_.].*[^_.]$")?change:null;
//        });
//        nameField.setTextFormatter(formatter);
//
//
//        final Label error = new Label("Name Field is require!");
//        error.setVisible(false);
//        nameField.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent> (){
//            @Override
//            public void handle(KeyEvent arg0) {
//                if(arg0.getCharacter().matches("[0-9]")){
//                    nameField.getStyleClass().add("error");
//                    error.setVisible(true);
//                    arg0.consume();
//                }else{
//                    error.setVisible(false);
//                    nameField.getStyleClass().remove("error");
//
//                }
//            }
//        });
//        Label n = new Label("Name");
//    }

}
