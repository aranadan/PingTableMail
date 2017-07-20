package pingTableMail.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pingTableMail.Conn;
import pingTableMail.IpHost;
import pingTableMail.Ping;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class MainController {

	public static final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	static final ObservableList<IpHost> ipDataList = FXCollections.observableArrayList();
    private final Conn database = new Conn();
	private final FileChooser fileChooser = new FileChooser();
	private final Stage stageFile = new Stage();
	private Stage stageDialog;
    private static final SimpleStringProperty isOnline = new SimpleStringProperty("UP");

	@FXML
	TextField secondsField;
	@FXML
	volatile  ToggleButton startStop_button;
	@FXML
	TableColumn<IpHost, String> hostName;
	@FXML
	TableColumn<IpHost, String> ip;
	@FXML
	TableColumn<IpHost, String> response;
	@FXML
	TableColumn<IpHost, Integer> time;
	@FXML
	TableView<IpHost> table;
	@FXML
	TextField mailLogin;
	@FXML
	PasswordField mailPass;
	@FXML
	public TextField mailTo;
	@FXML
	public  TextArea textArea = new TextArea();

    private final FXMLLoader fxmlLoader = new FXMLLoader();
    private DialogController dialogController;
    private Parent fxmlEdit;
    public static volatile boolean ISACTIVETHREAD = true;

	@FXML
	public void initialize() {

		try {
					database.Connect();
					database.CreateDB();
					database.ReadDB(ipDataList);
					textArea.appendText("Base connected");
					
				} catch (ClassNotFoundException | SQLException  e) {
					e.printStackTrace();
				}
		
		ip.setCellValueFactory(new PropertyValueFactory<>("ip"));
		hostName.setCellValueFactory(new PropertyValueFactory<>("hostName"));
		response.setCellValueFactory(new PropertyValueFactory<>("response"));
		time.setCellValueFactory(new PropertyValueFactory<>("time"));
		table.setItems(ipDataList);
		
		//add listener
		ipDataList.addListener((ListChangeListener<IpHost>) c -> {
            textArea.clear();
            textArea.appendText(String.valueOf(ipDataList.size()));
        });


        fxmlLoader.setLocation(getClass().getClassLoader().getResource("resources/fxml/edit.fxml"));
        dialogController = fxmlLoader.getController();
        try {
            fxmlEdit = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
		dialogController = fxmlLoader.getController();

        isOnline.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                if (!mailTo.getText().equals("") && mailTo.getText().contains("@")) {
                    System.out.println(isOnline.get());
                }
            }
        });
    }

	@FXML
	public void file_open() {

		fileChooser.setTitle("choice txt file");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("txt", "*.txt"));
		File file = fileChooser.showOpenDialog(stageFile);
		if (file != null) {
			initData(file);
		}
	}

	@FXML
	void file_save() throws IOException {
		fileChooser.setTitle("choice txt file");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("txt", "*.txt"));
		File file = fileChooser.showSaveDialog(stageFile);
		if (file!= null) {
            FileWriter writer = new FileWriter(file, false);

            for (IpHost s : ipDataList) {
                writer.write(s.getHostName() + ":" + s.getIp() + "\n");
            }
            writer.close();
        }
	}
	@FXML
	void save_to_db() throws IOException {
		
		try {
			database.WriteDB(ipDataList);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void delete() {
		ipDataList.remove(table.getSelectionModel().getSelectedIndex());
	}

	@FXML
	void clearLog() {
		textArea.clear();
	}

	@FXML
	public void startStop() {

        Loop pingLoop = new Loop();
        if (startStop_button.isSelected()) {
            pingLoop.start();
            ISACTIVETHREAD = true;
            System.out.println(pingLoop.getId() + " - Loop Thread start");
            textArea.clear();
        }else {
            pingLoop.interrupt();
            ISACTIVETHREAD = false;
            System.out.println(pingLoop.getId() + " stop");
        }
	}
	
	@FXML
	public void actionButtonPressed(ActionEvent actionEvent) {

        if (stageDialog == null) {
            stageDialog = new Stage();
            stageDialog.setResizable(false);
            stageDialog.setScene(new Scene(fxmlEdit));
            stageDialog.initModality(Modality.APPLICATION_MODAL);
        }

		Object source = actionEvent.getSource();

		if (!(source instanceof MenuItem)) {
				return;
			}

			MenuItem clickedItem = (MenuItem) source;
			IpHost selectedHost = table.getSelectionModel().getSelectedItem();
			
			switch (clickedItem.getId()) {
                case "btnAdd":
                    stageDialog.setTitle("Add Host ");
                    stageDialog.show();
                    break;

                case "btnEdit":
                    if (selectedHost == null)
                        {
                            textArea.clear();
                            textArea.appendText("host not selected");
                            break;
                        }

                stageDialog.setTitle("Edit Host");
                dialogController.edit(selectedHost);
                stageDialog.show();
			break;
			}
	}

	private void initData(File file) {
		try (Scanner scan = new Scanner(new FileReader(file))) {
			while (scan.hasNext()) {
				String tempString = scan.nextLine();
				if (!tempString.equals("")) {
				    String[] nameIP = tempString.split(":");
					ipDataList.add(new IpHost(nameIP[1],nameIP[0]));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}


	public class Loop extends Thread {

	   public void run() {

           for (IpHost anIpDataList : ipDataList) {

               //запускаю нить
               new Ping(anIpDataList).start();
           }


			while (startStop_button.isSelected()){

                try{
                    Thread.sleep(1000);		//Приостановка потока на 1 сек.
                    table.refresh();
                }catch(InterruptedException e){
                    return;							//Завершение потока после прерывания
                }

            }

       }
	}
}
