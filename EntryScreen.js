import React, { Component } from 'react';
import {
  Text,
  View,
  Alert
} from 'react-native';
import { Button, FormLabel, FormInput } from 'react-native-elements';
import { style } from "./Styles";

// Class for getting/updating entered values for use in EntryScreen
class FoodField extends Component {
  constructor(props) {
    super(props);
    this.state = {
      text: this.props.field
    };
  }

  extract(text) {
    if (this.props.field === "Food Name") {
      this.props.self.setState({title: text})
    } else if (this.props.field === "Quantity"){
      this.props.self.setState({noItems: text})
    } else {
      this.props.self.setState({content: text});
    }
  }

  render() {
    return (
      <View>
        <FormLabel>
          {this.props.field}
        </FormLabel>
        <FormInput
          onChangeText={(text) => {
            this.setState({text});
            this.extract(text);
          }}
          value={this.state.text}
          onFocus={() => {
            if (this.state.text === this.props.field) {
              this.setState({
                text: ""
              })
            }
          }}
        />
      </View>
    );
  }
}

export default class EntryScreen extends Component<{}> {
  constructor(props) {
    super(props);

    this.state = {
      title: '',
      noItems: '',
      content: ''
    }
  }

  componentWillUnmount(){
    //this.addFood();
    this.props.navigation.state.params.prevScreen._refreshFoods();
  }

  // Add food to Firebase
  addFood(name, quantity, content) {
    if (name != ('' | null) & quantity != ('' | null) & content != ('' | null)) {
      const fbhandler = this.props.navigation.state.params.fbhandler;
      const ref = fbhandler.addFood(name, parseInt(quantity, 10), content);
    } else Alert.alert('Please fill all fields');

    /* I don't think there's use for this here but I will leave it for now
    let newData = this.state.data;


    newData.push({
      key: ref.key,
      title: name,
      noItems: quantity,
      content: content
    });

    this.setState({
      data: newData
    });
    */
  }

  render() {
    return (
      <View>
        <FoodField field={"Food Name"} self={this} />
        <FoodField field={"Quantity"} self={this} />
        <FoodField field={"Content"} self={this} />
        <Button
          containerViewStyle={style.buttonContainer}
          buttonStyle={style.button}
          title='SAVE'
          onPress={() => this.addFood(this.state.title, this.state.noItems, this.state.content)}
        />
      </View>
    );
  }
}