package org.ugate.gui.components;

import java.util.LinkedHashSet;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.ugate.gui.components.BeanPathAdapterTest.Hobby;
import org.ugate.gui.components.BeanPathAdapterTest.Person;

public class ATest  extends Application {
	
	public static void main(final String[] args) {
		Application.launch(ATest.class, args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		VBox root = new VBox(10);
		Person person = new Person();
		Hobby hobby1 = new Hobby();
		hobby1.setName("Hobby 1");
		Hobby hobby2 = new Hobby();
		hobby2.setName("Hobby 2");
		person.setAllHobbies(new LinkedHashSet<Hobby>());
		person.getAllHobbies().add(hobby1);
		person.getAllHobbies().add(hobby2);
		BeanPathAdapter<Person> personPA = new BeanPathAdapter<>(person);
		ListView<String> lv = new ListView<>();
		personPA.bindContentBidirectional("allHobbies", "name", String.class, 
				lv.getItems(), String.class, null, null);
		ListView<String> lv2 = new ListView<>();
		personPA.bindContentBidirectional("allHobbies", "name", String.class, 
				lv2.getItems(), String.class, null, null);
		root.getChildren().addAll(lv, lv2);
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}

}
