import React, { Component } from 'react';
import {
  StyleSheet
} from 'react-native';
import { StackNavigator } from 'react-navigation';


import LoginScreen from './LoginScreen';
import HomeScreen from './HomeScreen';
import CupboardScreen from './CupboardScreen';
import RecipesScreen from './RecipesScreen';
import ListsScreen from './ListsScreen';
import CheckListScreen from './CheckListScreen';

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
      navigationOptions: {
        headerTitle: "My Cupboard"
      }
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
    CheckListS: {
      screen: CheckListScreen
    },
  },
);

export default RootNavigator;