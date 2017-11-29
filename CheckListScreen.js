import React, { Component } from 'react';
import {
  Text,
  View,
  StyleSheet,
  ListView,
  FlatList,
  TouchableOpacity,
  ScrollView
} from 'react-native';
import { List, ListItem, Button, CheckBox, FormInput} from 'react-native-elements'
import { style } from "./Styles";


class ListElement extends Component {
  constructor(props) {
    super(props);

    this.state = {
      checked: this.props.checked,
      title: this.props.title
    }
  }

  render() {
    return (
      <View style={style.listElement}>
        <CheckBox
          containerStyle={{
            backgroundColor: "rgba(0, 0, 0, 0)",
            margin: 10,
            marginLeft: 30,
            marginRight: 0,
            padding: 0
          }}
          checked={this.state.checked}
          onPress={() => this.setState({
            checked: !this.state.checked
          })}
        />
        <FormInput
          containerStyle={{
            width: "66%"
          }}
          value={this.state.title}
        />
      </View>
    );
  }
}

export default class CheckListScreen extends Component<{}> {
  constructor(props) {
    super(props);

    this.state = {
      elements: []
    };
  }

  static navigationOptions = ({ navigation, screenProps }) => ({
    title: navigation.state.params
  });

  // TODO: Load all the recipes from firebase in here into the data state
  componentDidMount(){
    this.setState({
      elements: [
        {
          elementName: "asdf",
          checked: false
        },
        {
          elementName: "asdasdasd",
          checked: true
        }
      ]
    });
  }

  render() {
    const { elements } = this.state;
    return (
      <ScrollView>
        {elements.map((elem)=>{
          return (
            <ListElement
              title={elem.elementName}
              checked={elem.checked}
            />
          );
        })}
      </ScrollView>
    );
  }
}