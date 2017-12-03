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

    this.state = {
      data: null,
    };
}

  createList() {
    const user = this.props.navigation.state.params;
    const id = user.uid;

    const ref = firebaseApp.database().ref('lists/' + id).push({
      title: "something",
      items: 0
    });

    let newData = this.state.data;
    newData.push({key: ref.key, title: "something"});

    this.setState({
      data: newData
    });
  }

  // TODO: Load all the recipes from firebase in here into the data state
  componentDidMount(){
    const user = this.props.navigation.state.params;
    const id = user.uid;
    const ref = firebaseApp.database().ref('lists/' + id);

    const data = [];
    const self = this;

    ref.on("value", function(snapshot) {
      if (snapshot.val()){
        Object.keys(snapshot.val()).forEach(function(key) {

          data.push({
            title: snapshot.val()[key].title,
            key: key
          });
        });

        self.setState({
          data: data
        });

        ref.off();
      } else {
        self.setState({
          data: []
        });
      }
    }, function (errorObject) {
      console.log("The read failed: " + errorObject.code);

      self.setState({
        data: []
      });
    });
  }

  render() {
    return (
      <View>
        <Button
          onPress={()=>{this.createList()}}
        />
        <FlatList
          data={this.state.data}
          renderItem={({item}) => <ListItem
            onPress={()=>{
              this.props.navigation.navigate("CheckListS", {
                user: this.props.navigation.state.params,
                list: item
              });
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