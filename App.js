/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  Platform,
  StyleSheet,
  Text,
  View,
  TextInput,
  Button,
  Alert
} from 'react-native';
import firebase from 'react-native-firebase';
import { StackNavigator } from 'react-navigation';
import LoginScreen from './LoginScreen';
import HomeScreen from './HomeScreen';

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

class SignIn extends Component {
  constructor(props) {
    super(props);
    this.state = {
      text: this.props.credential
    };
  }

  tempfunction(text) {
    if (this.props.credential === "Username") {
      this.props.self.setState({username: text});
    } else {
      this.props.self.setState({password: text});
    }
  }

  render() {
    return (
      <TextInput
        style={{height: 40, width: '75%', paddingLeft: 10, marginBottom: 10, borderColor: 'lightgray', borderWidth: 1}}
        onChangeText={(text) => {
            this.setState({text});
            this.tempfunction(text);
          }
        }
        onFocus={(text) => this.setState({text: ''})}
        value={this.state.text}
        secureTextEntry={this.props.secure}
      />
    );
  }
}

//export default class App extends Component<{}> {
class App extends Component<{}> {

  constructor(props) {
    super(props);

    this.state = {
      username: '',
      password: ''
    }
  }

  signIn() {
    firebase.auth().signInWithEmailAndPassword(this.state.username, this.state.password)
      .then((user) => {
        Alert.alert('Signed in successfully!');
      }).catch( (err) => {

        Alert.alert('Incorrect log in!');
    });
  }

  createUser() {
    firebase.auth().createUserWithEmailAndPassword(this.state.username, this.state.password)
      .then((user) => {
        Alert.alert('Created new account!');
      }).catch( (err) => {
        console.log(err);
        Alert.alert('Unable to create account.');
    });
  }

  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>
        </Text>
        <SignIn credential={"Username"} self={this} secure={false}/>
        <SignIn credential={"Password"} self={this} secure={true}/>
        <Button
          onPress={this.signIn.bind(this)}
          title="Sign in"
          color="darkblue"
        />
        <Button
          onPress={this.createUser.bind(this)}
          title="Create account"
          color="lightgreen"
        />
      </View>
    );
  }
}

//make sure this is near the bottom
const RootNavigator = StackNavigator({
    LoginS: { screen: LoginScreen},
    HomeS: { screen: HomeScreen },
});

export default RootNavigator;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    marginBottom: 20
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});
