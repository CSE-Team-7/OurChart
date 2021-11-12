package termproj;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class DoctorPage extends Pages{
	//private String username;
	private Doctor user = new Doctor();
	private String patientSelected;
	private ArrayList<Patient> patientList = new ArrayList<>();
	
	
	@FXML
	private ObservableList<Patient> obs = FXCollections.observableArrayList(user.getPatients());
	@FXML
	private ListView<Patient> patientView = new ListView<>(obs);
	
	@FXML 
	private Button sendScript;
	@FXML
	private Label welcomeLabel;
	@FXML
	private Button sendMsgButton;
	@FXML
	private TextField messageBody;
	@FXML
	private TextField subject;
	@FXML
	private TextArea prescript;
	@FXML
	private ListView<String> MedsView;
	@FXML
	private ListView<String> HealthView;
	@FXML
	private ListView<String> ImmunView;
	@FXML
	private Button reassignButton;
	@FXML
	private Label dobLabel;
	@FXML
	private Button RemoveMedsButton;
	@FXML
	private Button EnterMedsButton;
	@FXML
	private Button RemoveHealthButton;
	@FXML
	private Button EnterHealthButton;
	@FXML
	private Button RemoveImmunButton;
	@FXML
	private Button EnterImmunButton;
	@FXML
	private TextField EnterMedsTF;
	@FXML
	private TextField EnterHealthTF;
	@FXML
	private TextField EnterImmunTF;
	
	
	public DoctorPage(String un, ArrayList<User> uL, UserManager um) {
		super(un, uL, um);
		//find Doctor by username
		String ut = "Doctor";
		for(int i = 0; i < super.userList.size(); i++) {
			UserManager testUM = new UserManager(uL);
			System.out.println("Hannah user type:" + testUM.readUserFromList(super.username).getUserType());
			
			if(ut.equals(super.userList.get(i).getUserType()) && (super.userList.get(i).getUsername().equals(super.username))) {
				System.out.println(super.userList.get(i));
				user = (Doctor) super.userList.get(i);
			}
		}
		patientList = user.getPatients();
		System.out.print(patientList);
	}
	
	
	@FXML
	public void initialize() {
		welcomeLabel.setText("Welcome " +user.getFirstName());
		setListView();
		patientView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				userSelected(event);
			}
		});
		setListView();

	}
	@FXML
	public void changeDoc(Event e) {
		Doctor currentDoctor = user;
		Doctor newDoctor;
		ArrayList<Doctor> dList = new ArrayList<>();
		Patient selectedPatient = (Patient) patientView.getSelectionModel().getSelectedItem();
		for(int i = 0; i < umgr.getUserList().size(); i++) {
			if(umgr.getUserList().get(i).getUserType().equals("Doctor")) {
				dList.add((Doctor) umgr.getUserList().get(i));
			}
		}
		if(dList.size() > 1) {
			Random rand = new Random();
			int randInt;
			do {
				randInt = rand.nextInt(dList.size());
				newDoctor = dList.get(randInt);
			}while(currentDoctor.equals(newDoctor));
			
			newDoctor.addPatient(selectedPatient);
			selectedPatient.setDoctor(newDoctor.getID());
			currentDoctor.removePatient(selectedPatient);
			}
		else {
			System.out.println("Only 1 doctor");
		}
		umgr.writeAllUsers();
		setListView();
	}
	
	public void setListView() {
		obs.setAll(patientList);
		patientView.setItems(obs);
	}
	
	public void userSelected(MouseEvent arg0) {
		Patient selectedPatient = (Patient) patientView.getSelectionModel().getSelectedItem();
		dobLabel.setText(selectedPatient.getDOB());
		
		ArrayList<String> tempList = new ArrayList<>();
		for (int i = 0; i < selectedPatient.getMedications().size(); i++) {
			tempList.add(selectedPatient.getMedications().get(i));
		}
		ObservableList<String> stringList = FXCollections.observableArrayList(tempList);
		stringList.setAll(tempList);
		MedsView.setItems(stringList);
		
		tempList = new ArrayList<>();
		for (int i = 0; i < selectedPatient.getHealthIssues().size(); i++) {
			tempList.add(selectedPatient.getHealthIssues().get(i));
		}
		stringList = FXCollections.observableArrayList(tempList);
		stringList.setAll(tempList);
		HealthView.setItems(stringList);
		
		tempList = new ArrayList<>();
		for (int i = 0; i < selectedPatient.getImmunizations().size(); i++) {
			tempList.add(selectedPatient.getImmunizations().get(i));
		}
		stringList = FXCollections.observableArrayList(tempList);
		stringList.setAll(tempList);
		ImmunView.setItems(stringList);
		
	}
	
	public void sendScript(ActionEvent event) throws IOException {
		sendPrescriptions();
		
	}
	
	public void sendPrescriptions() {
		Patient selectedPatient = (Patient) patientView.getSelectionModel().getSelectedItem();
		String newprescription = prescript.getText();
		int l = (selectedPatient.getPrescriptions().length + 1);
		String[] newPrescriptions = new String[l];
		
		for(int i = 0; i < selectedPatient.getPrescriptions().length; i++){
		   newPrescriptions[i] = selectedPatient.getPrescriptions()[i];
		}
		newPrescriptions[selectedPatient.getPrescriptions().length] = newprescription;
		selectedPatient.setPrescriptions(newPrescriptions);
		
	String subj = ("New Prescription " + newprescription);
	String body = ("Hello " + selectedPatient.getFirstName() + " your prescription " + newprescription 
			+ " has been sent to your pharmacy " + selectedPatient.getPharmacy() + " and will be ready soon");
	String[] recipient = {patientSelected};
	String senderUN = selectedPatient.getUsername();
	MessageHandler msgHandler = new MessageHandler(subj,body,senderUN);
	msgHandler.sendMessage();
		
	}
	public void send(ActionEvent event) throws IOException {
		sendMsg();
	}
	
	public void sendMsg() {
		String subj = subject.getText();
		String body = messageBody.getText();
		String senderUN = username;
		PersonnelFileReader reader = new PersonnelFileReader(username);
		Doctor sender = (Doctor)reader.readUser();//changed readEmployee to readUser
		String[] recipient = {patientSelected};
		//PatientMessage msg = new PatientMessage(subj,body,senderUN,recipient);
		MessageHandler msgHandler = new MessageHandler(subj,body,senderUN);
		msgHandler.sendMessage();
	}
	
	
	@FXML
	public void removeMeds(Event e) {
		Patient selectedPatient = (Patient) patientView.getSelectionModel().getSelectedItem();
		if(selectedPatient.getMedications().contains(MedsView.getSelectionModel().getSelectedItem())) {
			selectedPatient.getMedications().remove(MedsView.getSelectionModel().getSelectedIndex());
		}
		
		resetPatientHistoryView(selectedPatient);
	}
	@FXML
	public void addMeds(Event e) {
		Patient selectedPatient = (Patient) patientView.getSelectionModel().getSelectedItem();
		selectedPatient.getMedications().add(EnterMedsTF.getText());
		resetPatientHistoryView(selectedPatient);
	}
	@FXML
	public void removeHealth(Event e) {
		Patient selectedPatient = (Patient) patientView.getSelectionModel().getSelectedItem();
		if(selectedPatient.getHealthIssues().contains(HealthView.getSelectionModel().getSelectedItem())) {
			selectedPatient.getHealthIssues().remove(HealthView.getSelectionModel().getSelectedIndex());
		}
		
		resetPatientHistoryView(selectedPatient);
	}
	@FXML
	public void addHealth(Event e) {
		Patient selectedPatient = (Patient) patientView.getSelectionModel().getSelectedItem();
		selectedPatient.getHealthIssues().add(EnterHealthTF.getText());
		resetPatientHistoryView(selectedPatient);
	}
	@FXML
	public void removeImmunizations(Event e) {
		Patient selectedPatient = (Patient) patientView.getSelectionModel().getSelectedItem();
		if(selectedPatient.getImmunizations().contains(ImmunView.getSelectionModel().getSelectedItem())) {
			selectedPatient.getImmunizations().remove(ImmunView.getSelectionModel().getSelectedIndex());
		}
		
		resetPatientHistoryView(selectedPatient);
	}
	@FXML
	public void addImmunizations(Event e) {
		Patient selectedPatient = (Patient) patientView.getSelectionModel().getSelectedItem();
		selectedPatient.getImmunizations().add(EnterImmunTF.getText());
		resetPatientHistoryView(selectedPatient);
	}
	private void resetPatientHistoryView(Patient selectedPatient) {
		ArrayList<String> tempList = new ArrayList<>();
		for (int i = 0; i < selectedPatient.getMedications().size(); i++) {
			tempList.add(selectedPatient.getMedications().get(i));
		}
		ObservableList<String> stringList = FXCollections.observableArrayList(tempList);
		stringList.setAll(tempList);
		MedsView.setItems(stringList);
		
		tempList = new ArrayList<>();
		for (int i = 0; i < selectedPatient.getHealthIssues().size(); i++) {
			tempList.add(selectedPatient.getHealthIssues().get(i));
		}
		stringList = FXCollections.observableArrayList(tempList);
		stringList.setAll(tempList);
		HealthView.setItems(stringList);
		
		tempList = new ArrayList<>();
		for (int i = 0; i < selectedPatient.getImmunizations().size(); i++) {
			tempList.add(selectedPatient.getImmunizations().get(i));
		}
		stringList = FXCollections.observableArrayList(tempList);
		stringList.setAll(tempList);
		ImmunView.setItems(stringList);
		umgr.writeAllUsers();
	}

}
