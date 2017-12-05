import React, { Component } from 'react';
import {
  Alert,
  TouchableOpacity
} from 'react-native';
import { StackNavigator } from 'react-navigation';
import { Icon } from 'react-native-elements';


import LoginScreen from './LoginScreen';
import HomeScreen from './HomeScreen';
import CupboardScreen from './CupboardScreen';
import RecipesScreen from './RecipesScreen';
import ListsScreen from './ListsScreen';
import EntryScreen from './EntryScreen';
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
        headerTitle: "Shopping Lists"
      }
    },
    EntryS: {
      screen: EntryScreen,
      navigationOptions: {
        headerTitle: "Enter An Item"
      }
    },
    CheckListS: {
      screen: CheckListScreen
    }
  },
);

export default RootNavigator;