import React, { Component } from 'react';
import {
  Text,
  View,
  Alert
} from 'react-native';
import { Button, FormLabel, FormInput } from 'react-native-elements';
import { style } from "./Styles";

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

  render() {
    return (
      <View>
        <FoodField field={"Food Name"} self={this} />
        <FoodField field={"Quantity"} self={this} />
        <FoodField field={"Content"} self={this} />
        <Button
          containerViewStyle={style.buttonContainer}
          buttonStyle={style.button}
          title='SUBMIT'
        />
      </View>
    );
  }
}