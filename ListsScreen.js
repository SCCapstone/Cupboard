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

import firebase from 'react-native-firebase';

const firebaseConfig = {
  apiKey: 'AIzaSyDGvm-2kkreAPcffUHWCH5HaWlHas6Cnkg',
  authDomain: 'the-cupboard-app.firebaseapp.com',
  databaseURL: 'https://the-cupboard-app.firebaseio.com/',
  storageBucket: 'gs://the-cupboard-app.appspot.com',
  appId: '1:449840930413:android:7f854998b9cb29a1',
  messagingSenderId: '449840930413',
  projectId: 'the-cupboard-app'
};

const firebaseApp = firebase.initializeApp(firebaseConfig);

export default class ListsScreen extends Component<{}> {
  constructor(props) {
    super(props);

    this.user = this.props.navigation.state.params;
    // this.itemsRef = firebaseApp.database().ref("lists/" + id);

    this.state = {
      data: null,
      key: 1,
    };
}

  addToList() {
    const user = this.props.navigation.state.params;
    const id = user.uid;

    console.log(firebaseApp.database().ref('lists/UGxPakKb3FQNCqreeY04CO4Z2r23'));

    // firebaseApp.database().ref('lists/' + id).set({
    //   data: 0
    // });

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