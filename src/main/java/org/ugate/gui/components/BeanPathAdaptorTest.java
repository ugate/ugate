package org.ugate.gui.components;

import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.scene.control.Slider;
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
	private static final String P1_LABEL = "Person 1";
	private static final String P2_LABEL = "Person 2";
	private final Person person1 = new Person();
	private final Person person2 = new Person();
	private final BeanPathAdaptor<Person> personPA = new BeanPathAdaptor<Person>(person1);
	
	public static void main(final String[] args) {
		Application.launch(BeanPathAdaptorTest.class, args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle(BeanPathAdaptor.class.getSimpleName() + " TEST");
		pojoTA.setFocusTraversable(false);
		pojoTA.setWrapText(true);
		pojoTA.setEditable(false);
		pBox = new ChoiceBox<>(FXCollections.observableArrayList(
				P1_LABEL, P2_LABEL));
		pBox.getSelectionModel().select(0);
		pBox.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				personPA.setBean(newValue == P1_LABEL ? person1 : person2);
			}
		});
		pBox.autosize();
		ToolBar toolBar = new ToolBar();
		toolBar.getItems().add(pBox);
		VBox personBox = new VBox(10);
		personBox.setPadding(new Insets(10, 10, 10, 50));
		FlowPane beanPane = new FlowPane();
		personBox.getChildren().addAll(
				new Text("Person POJO using auto-generated JavaFX properties:"), 
				beanTF("name", 50, null, "[a-zA-z0-9\\s]*"),
				beanTF("age", 100, Slider.class, null),
				beanTF("address.street", 50, null, "[a-zA-z0-9\\s]*"),
				beanTF("address.location.state", 2, ChoiceBox.class, 
						"[a-zA-z]", STATES),
				beanTF("address.location.country", 10, null, "[0-9]"),
				beanTF("address.location.international", 0, 
						CheckBox.class, null),
				new Label("POJO Dump:"),
				pojoTA);
		beanPane.getChildren().addAll(personBox);

		final TextField pojoNameTF = new TextField();
		Button pojoNameBtn = new Button("Set Person's Name");
		pojoNameBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				personPA.getBean().setName(pojoNameTF.getText());
				dumpPojo(personPA);
			}
		});
		VBox pojoBox = new VBox(10);
		pojoBox.setPadding(new Insets(10, 10, 10, 10));
		pojoBox.getChildren().addAll(new Label("Set person fields via POJO:"),
				new Label("Name:"), pojoNameTF, pojoNameBtn);
		
		VBox beanBox = new VBox(10);
		beanBox.getChildren().addAll(toolBar, beanPane);
		SplitPane root = new SplitPane();
		root.getItems().addAll(beanBox, pojoBox);
		primaryStage.setOnShowing(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				dumpPojo(personPA);
			}
		});
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}
	
	@SafeVarargs
	public final void dumpPojo(final BeanPathAdaptor<Person>... ps) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				String dump = "";
				for (BeanPathAdaptor<Person> p : ps) {
					dump += "Person 1 {name=" + 
							p.getBean().getName() +
							", age=" +
							p.getBean().getAge() +
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
		});
	}
	
	public <T> HBox beanTF(String path, final int maxChars, 
			Class<? extends Control> controlType, 
			final String restictTo, 
			@SuppressWarnings("unchecked") T... choices) {
		HBox box = new HBox();
		Control ctrl;
		if (controlType == CheckBox.class) {
			CheckBox cb = new CheckBox();
			cb.selectedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable,
						Boolean oldValue, Boolean newValue) {
					dumpPojo(personPA);
				}
			});
			// POJO binding magic...
			personPA.bindBidirectional(path, cb.selectedProperty());
			ctrl = cb;
		} else if (controlType == ChoiceBox.class) {
			ChoiceBox<T> cb = new ChoiceBox<>(
					FXCollections.observableArrayList(choices));
			cb.valueProperty().addListener(new InvalidationListener() {
				@Override
				public void invalidated(Observable observable) {
					dumpPojo(personPA);
				}
			});
			// POJO binding magic...
			personPA.bindBidirectional(path, cb.valueProperty());
			ctrl = cb;
		} else if (controlType == Slider.class) {
			Slider sl = new Slider();
			sl.setShowTickLabels(true);
			sl.setShowTickMarks(true);
			sl.setMajorTickUnit(maxChars/2);
			sl.setMinorTickCount(7);
			sl.setBlockIncrement(1);
			sl.setMax(maxChars+1);
			sl.setSnapToTicks(true);
			sl.valueProperty().addListener(new InvalidationListener() {
				@Override
				public void invalidated(Observable observable) {
					dumpPojo(personPA);
				}
			});
			// POJO binding magic...
			personPA.bindBidirectional(path, sl.valueProperty());
			ctrl = sl;
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
			    			(getText() == null || getText().length() < maxChars));
			    }
			};
			tf.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> observable,
						String oldValue, String newValue) {
					dumpPojo(personPA);
				}
			});
			// POJO binding magic...
			personPA.bindBidirectional(path, tf.textProperty());
			ctrl = tf;
		}
		box.getChildren().addAll(new Label(path + " = "), ctrl);
		return box;
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
		private double age;
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
		public double getAge() {
			return age;
		}
		public void setAge(double age) {
			this.age = age;
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
