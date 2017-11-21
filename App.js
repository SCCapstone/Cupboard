import React, { Component } from 'react';
import {
  StyleSheet
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

//make sure this is near the bottom
const RootNavigator = StackNavigator(
  {
    LoginS: {screen: LoginScreen},
    HomeS: {screen: HomeScreen},
  },
  {
    // headerMode: 'screen'
  }
);

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
