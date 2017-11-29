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
      data: null,
      key: 1
    };
  }

  static navigationOptions = ({ navigation, screenProps }) => ({
    title: navigation.state.params
  });

  addToList() {
    let newKey = this.state.key;
    let newData = this.state.data;

    newData.push({
      key: this.state.key,
      title: "something"
    });

    newKey += 1;

    this.setState({
      data: newData,
      key: newKey
    });
  }

  // TODO: Load all the recipes from firebase in here into the data state
  componentDidMount(){
    this.setState({
      data: [
        {
          key: 99,
          title: "asdf",
          checked: false
        },
        {
          key: 20,
          title: "asdasdasd",
          checked: true
        }
      ]
    });
  }

  render() {
    return (
      <View>
        <Button
          onPress={()=>{this.addToList()}}
        />
        <FlatList
          data={this.state.data}
          renderItem={({item}) => <ListElement
            checked={item.checked}
            title={item.title}
          />}
          keyExtractor={(item, index) => item.key}
          extraData={this.state}
        />
      </View>
    );
  }
}