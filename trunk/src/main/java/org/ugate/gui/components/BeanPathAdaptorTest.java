package org.ugate.gui.components;

import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class BeanPathAdaptorTest extends Application {
	
	ChoiceBox<String> pBox;
	TextArea pojoTA = new TextArea();
	public static final String[] STATES = new String[]{"AK","AL","AR","AS","AZ","CA","CO",
		"CT","DC","DE","FL","GA","GU","HI","IA","ID","IL","IN","KS","KY","LA","MA","MD",
		"ME","MH","MI","MN","MO","MS","MT","NC","ND","NE","NH","NJ","NM","NV","NY","OH",
		"OK","OR","PA","PR","PW","RI","SC","SD","TN","TX","UT","VA","VI","VT","WA","WI",
		"WV","WY"};
	private BeanPathAdaptor<Person> person1 = new BeanPathAdaptor<Person>(new Person());
	private BeanPathAdaptor<Person> person2 = new BeanPathAdaptor<Person>(new Person());
	
	public static void main(final String[] args) {
		Application.launch(BeanPathAdaptorTest.class, args);
		final Person person = new Person();
		final BeanPathAdaptor<Person> personBA = new BeanPathAdaptor<Person>(person);
		final TextField tf = new TextField("Nothing");
		personBA.bindBidirectional("address.street", tf.textProperty());
		System.out.println("POJO Street: " + person.getAddress().getStreet());
		System.out.println("Adaptor Street: " + tf.getText());
		tf.setText("Something");
		System.out.println("POJO Street: " + person.getAddress().getStreet());
		System.out.println("Adaptor Street: " + tf.getText());
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle(BeanPathAdaptor.class.getSimpleName() + " TEST");
		pojoTA.setFocusTraversable(false);
		pojoTA.setWrapText(true);
		pojoTA.setEditable(false);
		pBox = new ChoiceBox<>(FXCollections.observableArrayList(
				"Person 1", "Person 2"));
		pBox.getSelectionModel().select(0);
		pBox.autosize();
		ToolBar toolBar = new ToolBar();
		toolBar.getItems().add(pBox);
		VBox personBox = new VBox(10);
		personBox.setPadding(new Insets(10, 10, 10, 50));
		FlowPane beanPane = new FlowPane();
		Button dumpPojoBtn = new Button("Dump Person POJO");
		dumpPojoBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				dumpPojo(getSelectedAdaptor());
			}
		});
		personBox.getChildren().addAll(
				new Text("Person POJO using auto-generated JavaFX properties:"), 
				beanTF("name", 50, null, "[a-zA-z0-9\\s]*"), 
				beanTF("address.street", 50, null, "[a-zA-z0-9\\s]*"),
				beanTF("address.location.state", 2, ChoiceBox.class, 
						"[a-zA-z]", STATES),
				beanTF("address.location.country", 10, null, "[0-9]"),
				beanTF("address.location.international", 0, 
						CheckBox.class, null),
				new Label("POJO Dump:"),
				pojoTA);
		beanPane.getChildren().addAll(personBox);
		
		VBox beanBox = new VBox(10);
		beanBox.getChildren().addAll(toolBar, beanPane);
		SplitPane root = new SplitPane();
		root.getItems().addAll(beanBox);
		primaryStage.setOnShowing(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				dumpPojo(getSelectedAdaptor());
			}
		});
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}
	
	@SafeVarargs
	public final void dumpPojo(BeanPathAdaptor<Person>... ps) {
		String dump = "";
		for (BeanPathAdaptor<Person> p : ps) {
			dump += "Person 1 {name=" + 
					p.getBean().getName() +
					", address.street=" +
					p.getBean().getAddress().getStreet() +
					", address.location.state=" + 
					p.getBean().getAddress().getLocation().getState() +
					", address.location.country=" +
					p.getBean().getAddress().getLocation().getCountry() +
					", address.location.international=" +
					p.getBean().getAddress().getLocation().isInternational() +
					"}\n";
		}
		pojoTA.setText(dump);
	}
	
	public <T> HBox beanTF(String path, final int maxChars, 
			Class<? extends Control> controlType, 
			final String restictTo, 
			@SuppressWarnings("unchecked") T... choices) {
		HBox box = new HBox();
		final BeanPathAdaptor<Person> bpa = getSelectedAdaptor();
		Control ctrl;
		if (controlType == CheckBox.class) {
			CheckBox cb = new CheckBox();
			cb.selectedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable,
						Boolean oldValue, Boolean newValue) {
					dumpPojo(bpa);
				}
			});
			// POJO binding magic...
			bpa.bindBidirectional(path, cb.selectedProperty());
			ctrl = cb;
		} else if (controlType == ChoiceBox.class) {
			ChoiceBox<T> cb = new ChoiceBox<>(
					FXCollections.observableArrayList(choices));
			cb.valueProperty().addListener(new InvalidationListener() {
				@Override
				public void invalidated(Observable observable) {
					dumpPojo(bpa);
				}
			});
			// POJO binding magic...
			bpa.bindBidirectional(path, cb.valueProperty());
			ctrl = cb;
		} else {
			final TextField tf = new TextField() {
				@Override 
				public void replaceText(int start, int end, String text) {
			        if (matchTest(text)) {
			            super.replaceText(start, end, text);
			        }
			    }
			    @Override 
			    public void replaceSelection(String text) {
			        if (matchTest(text)) {
			            super.replaceSelection(text);
			        }
			    }
			    private boolean matchTest(String text) {
			    	return text.isEmpty() || (text.matches(restictTo) && 
			    			getText().length() < maxChars);
			    }
			};
			tf.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> observable,
						String oldValue, String newValue) {
					dumpPojo(bpa);
				}
			});
			// POJO binding magic...
			bpa.bindBidirectional(path, tf.textProperty());
			ctrl = tf;
		}
		box.getChildren().addAll(new Label(path + " = "), ctrl);
		return box;
	}
	
	public BeanPathAdaptor<Person> getSelectedAdaptor() {
		return pBox.getSelectionModel() == null || 
				pBox.getSelectionModel().getSelectedIndex() <= 0 ? 
				person1 : person2;
	}
	
	public HBox beanTFW(String startLabel, String endLabel, TextField... tfs) {
		HBox box = new HBox();
		box.getChildren().add(new Label(startLabel + '('));
		box.getChildren().addAll(tfs);
		box.getChildren().add(new Label(endLabel + ");"));
		return box;
	}
	
	public static class Person {
		private String name;
		private Address address;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Address getAddress() {
			return address;
		}
		public void setAddress(Address address) {
			this.address = address;
		}
	}
	public static class Address {
		private String street;
		private Location location;
		public String getStreet() {
			return street;
		}
		public void setStreet(String street) {
			this.street = street;
		}
		public Location getLocation() {
			return location;
		}
		public void setLocation(Location location) {
			this.location = location;
		}
	}
	public static class Location {
		private int country;
		private String state;
		private Boolean isInternational;
		public int getCountry() {
			return country;
		}
		public void setCountry(int country) {
			this.country = country;
		}
		public String getState() {
			return state;
		}
		public void setState(String state) {
			this.state = state;
		}
		public Boolean isInternational() {
			return isInternational;
		}
		public void setInternational(Boolean isInternational) {
			this.isInternational = isInternational;
		}
	}

}
