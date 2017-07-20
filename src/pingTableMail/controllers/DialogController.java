package pingTableMail.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pingTableMail.IpHost;
import pingTableMail.interfaces.EditHost;

public class DialogController implements EditHost{

    @FXML
	TextField nameField;
	@FXML
	TextField ipField;
	@FXML
    Label labelMessage;

    @FXML
	public void btnOk() {
        IpHost ipHost = new IpHost();
	    for (IpHost checkedHost : MainController.ipDataList){
            if (ipField.getText().toLowerCase().equals(checkedHost.getIp().toLowerCase())){
                delete(checkedHost);
                break;
            }
        }

        if (ipField.getText().equals("")) {
            labelMessage.setText("enter ip!");
            return;
        }

        add(ipHost);
        labelMessage.setText("host added");
	    ipField.clear();
	    nameField.clear();
    }


	@FXML
	public void btnCancel(ActionEvent actionEvent) {
	    Node source = (Node) actionEvent.getSource();
	    Stage stage = (Stage) source.getScene().getWindow();
	    nameField.clear();
	    ipField.clear();
	    labelMessage.setText("");
	    stage.hide();
    }

    @Override
    public void add(IpHost host) {
        host.setHostName(nameField.getText());
        host.setIp(ipField.getText());
        MainController.ipDataList.add(host);
    }

    @Override
    public void edit(IpHost host) {
            nameField.setText(host.getHostName());
            ipField.setText(host.getIp());
    }

    @Override
    public void delete(IpHost host) {
        MainController.ipDataList.remove(host);
    }
}
