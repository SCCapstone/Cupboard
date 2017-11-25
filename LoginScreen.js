import React, { Component } from 'react';
import {
  StyleSheet,
  Text,
  View,
  Alert
} from 'react-native';
import firebase from 'react-native-firebase';
import { FormLabel, FormInput, Button} from 'react-native-elements'
import { style } from './Styles'

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

  extract(text) {
    if (this.props.credential === "Email") {
      this.props.self.setState({username: text})
    } else {
      this.props.self.setState({password: text});
    }
  }

  render() {
    return (
      <View>
        <FormLabel>
          {this.props.credential}
        </FormLabel>
        <FormInput
          onChangeText={(text) => {
            this.setState({text});
            this.extract(text);
          }}
          value={this.state.text}
          onFocus={() => {
            if (this.state.text === this.props.credential) {
              this.setState({
                text: ""
              })
            }
          }}
          secureTextEntry={this.props.secure}
        />
      </View>
    );
  }
}

export default class LoginScreen extends Component<{}> {

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
        this.props.navigation.navigate('HomeS');
      }).catch( (err) => {
      Alert.alert('Incorrect log in!');
    });
  }

  createUser() {
    firebase.auth().createUserWithEmailAndPassword(this.state.username, this.state.password)
      .then( (user) => {
        Alert.alert('Created new account!');
      }).catch( (err) => {
      console.log(err);
      Alert.alert('Unable to create account.');
    });
  }

  render() {
    const { navigate } = this.props.navigation;
    return (
      <View style={style.containerCenterContent}>
        <Text style={style.title}>
          Welcome to Cupboard!
        </Text>
        <SignIn credential={"Email"} self={this} secure={false}/>
        <SignIn credential={"Password"} self={this} secure={true}/>
        <View style={style.buttons}>
          <Button
            containerViewStyle={style.buttonContainer}
            buttonStyle={style.button}
            backgroundColor="white"
            onPress={this.signIn.bind(this)}
            title="SIGN IN"
            raised
            color="black"
          />
          <Button
            containerViewStyle={style.buttonContainer}
            buttonStyle={style.button}
            backgroundColor="#875F9A"
            onPress={() => {
              this.createUser.bind(this);
            }}
            title="CREATE ACCOUNT"
            color="white"
            raised
          />
          <Button
            containerViewStyle={style.buttonContainer}
            buttonStyle={style.button}
            onPress={() => {navigate('HomeS')}}
            title="HOME"
            color="white"
            backgroundColor="#9D2933"
            raised
          />
        </View>
      </View>
    );
  }
}