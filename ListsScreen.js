import React, { Component } from 'react';
import {
  Text,
  View,
  StyleSheet,
  ListView,
  FlatList,
  TouchableOpacity
} from 'react-native';
import { List, ListItem, Button } from 'react-native-elements'
import { style } from "./Styles";


const list = [
  {
    title: 'Appointments',
  },
  {
    title: 'Trips',
  },
  {
    title: 'New List',
  },
];

export default class ListsScreen extends Component<{}> {

  constructor(props) {
    super(props);

    this.state = {
      data: list
    };
  }

  addToList() {
    let newData = this.state.data;
    newData.push([{title: "something"}]);

    this.setState({
      data: newData
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
          renderItem={({item}) => <ListItem
            key={item.title}
            title={item.title}
            leftIcon={{name: item.icon}}
          />}
          keyExtractor={(item, index) => item.title}
          extraData={this.state}
        />

      </View>
    );
  }
}