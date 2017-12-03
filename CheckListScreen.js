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

class ListElement extends Component {
  constructor(props) {
    super(props);

    this.state = {
      checked: this.props.checked,
      title: this.props.title,
    };
  }

  _onChange(text){

    const user = this.props.params.user;
    const id = user.uid;
    const listid = this.props.params.list.key;

    const self = this;
    const itemid = this.props.id;

    let alldata = this.props.self.state.data;

    alldata.map((elem)=>{
      if (elem.id === itemid){
        elem.title = text;
        return elem;
      }
    });

    this.props.self.setState({
      data: alldata
    });

    this.setState({
      title: text
    });
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
          onChangeText={(text)=>{
            this._onChange(text);
          }}
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
    };
  }

  static navigationOptions = ({ navigation, screenProps }) => ({
    title: navigation.state.params.list.title
  });

  addToList() {
    const user = this.props.navigation.state.params.user;
    const id = user.uid;

    const listid = this.props.navigation.state.params.list.key;

    const ref = firebaseApp.database().ref('lists/' + id + '/' + listid + '/items').push({
      title: "something",
      checked: false
    });

    let newData = this.state.data;
    newData.push({
      key: ref.key,
      title: "something",
      checked: false
    });

    this.setState({
      data: newData
    });
  }

  saveList() {
    const user = this.props.navigation.state.params.user;
    const id = user.uid;
    const listid = this.props.navigation.state.params.list.key;

    const data = this.state.data;
    const json = {};


    data.forEach((elem) => {
      json[elem.id] = {
        checked: elem.checked,
        title: elem.title
      };
    });

    const ref = firebaseApp.database().ref('lists/' + id + '/' + listid + '/items').set(json);
  }

  // TODO: Load all the recipes from firebase in here into the data state
  componentDidMount(){
    const user = this.props.navigation.state.params.user;
    const id = user.uid;

    const listid = this.props.navigation.state.params.list.key;

    const ref = firebaseApp.database().ref('lists/' + id + '/' + listid + '/items');

    const data = [];
    const self = this;

    ref.on("value", function(snapshot) {
      if (snapshot.val()){
        Object.keys(snapshot.val()).forEach(function(key) {
          data.push({
            title: snapshot.val()[key].title,
            checked: snapshot.val()[key].checked,
            id: key
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
          onPress={()=>{this.addToList()}}
          title={"add element"}
        />
        <Button
          onPress={()=>{this.saveList()}}
          backgroundColor={"green"}
          title={"save to firebase"}
        />
        <FlatList
          data={this.state.data}
          renderItem={({item}) => <ListElement
            params={this.props.navigation.state.params}
            checked={item.checked}
            title={item.title}
            id={item.id}
            self={this}
          />}
          keyExtractor={(item, index) => item.id}
          extraData={this.state}
        />
      </View>
    );
  }
}