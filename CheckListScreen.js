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
      checked: false,
      title: "temp"
    }
  }

  render() {
    return (
      <View style={style.listElement}>
        <CheckBox
          containerStyle={{
            backgroundColor: "rgba(0, 0, 0, 0)",
          }}
          checked={this.state.checked}
          onPress={() => this.setState({ checked: !this.state.checked })}
        />
        <FormInput
          containerStyle={{
          }}
        />
      </View>
    );
  }
  }

export default class CheckListScreen extends Component<{}> {
  constructor(props) {
    super(props);
  }

  static navigationOptions = ({ navigation, screenProps }) => ({
    title: navigation.state.params
  });

  // TODO: Load all the recipes from firebase in here into the data state
  componentDidMount(){

  }
  render() {
    return (
      <ScrollView>
        <ListElement/>
        <ListElement/>
      </ScrollView>
    );
  }
}