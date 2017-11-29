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
import styles from "./CheckListScreen";

export default class ListsScreen extends Component<{}> {
  constructor(props) {
    super(props);

    this.state = {
      data: null,
      key: 1
    };
}

  addToList() {
    let newKey = this.state.key;
    let newData = this.state.data;
    newData.push({key: this.state.key, title: "something"});

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
          key: 'asdf',
          title: 'Appointments',
        },
        {
          key: 'jkl',
          title: 'Trips',
        },
        {
          key: 'lmnop',
          title: 'New List',
        },
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
          renderItem={({item}) => <ListItem
            onPress={()=>{
              this.props.navigation.navigate("CheckListS", item.title);
            }}
            key={item.key}
            title={item.title}
          />}
          keyExtractor={(item, index) => item.key}
          extraData={this.state}
        />
      </View>
    );
  }
}