import React, { Component } from 'react';
import {
  Alert,
  TouchableOpacity
} from 'react-native';
import firebase from 'react-native-firebase';
import { StackNavigator } from 'react-navigation';
import { Icon } from 'react-native-elements';


import LoginScreen from './LoginScreen';
import HomeScreen from './HomeScreen';
import CupboardScreen from './CupboardScreen';
import RecipesScreen from './RecipesScreen';
import ListsScreen from './ListsScreen';
import EntryScreen from './EntryScreen';


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
    LoginS: {
      screen: LoginScreen,
      navigationOptions: {
        header: null
      }
    },
    HomeS: {
      screen: HomeScreen,
      navigationOptions: {
        headerTitle: "Home"
      }
    },
    CupboardS: {
      screen: CupboardScreen,
      navigationOptions: ({ navigation, screenProps}) => ({
        headerTitle: "My Cupboard",
        headerRight:
          <TouchableOpacity onPress={() => navigation.navigate('EntryS')}>
            <Icon name="circle-with-plus"
                  type="entypo"
            />
          </TouchableOpacity>
      })
    },
    RecipesS: {
      screen: RecipesScreen,
      navigationOptions: {
        headerTitle: "My Recipes"
      }
    },
    ListsS: {
      screen: ListsScreen,
      navigationOptions: {
        headerTitle: "My Shopping Lists"
      }
    },
    EntryS: {
      screen: EntryScreen,
      navigationOptions: {
        headerTitle: "Enter An Item"
      }
    },
  },
);

export default RootNavigator;