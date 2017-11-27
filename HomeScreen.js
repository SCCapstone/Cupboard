import React, { Component } from 'react';
import {
  Text,
  View,
  StyleSheet
} from 'react-native';
import { Button } from 'react-native-elements';
import { style } from "./Styles";
import firebase from 'react-native-firebase';

export default class HomeScreen extends Component<{}> {
    signOut() {
        firebase.auth().signOut()
            .then((user) => {
                this.props.navigation.navigate('LoginS');
            }).catch( (err) => {
            Alert.alert('Sign Out Failed!');
        });
    }
  render() {
    return (
      <View style={style.containerCenterContent}>
        <Button
          containerViewStyle={style.buttonContainer}
          buttonStyle={style.button}
          title='MY CUPBOARD'
          onPress={() => {
            this.props.navigation.navigate("CupboardS");
          }}
        />
        <Button
          title='RECIPES'
          containerViewStyle={style.buttonContainer}
          buttonStyle={style.button}
          onPress={() => {
            this.props.navigation.navigate("RecipesS");
          }}
        />
        <Button
          title='SHOPPING LISTS'
          containerViewStyle={style.buttonContainer}
          buttonStyle={style.button}
          onPress={() => {
            this.props.navigation.navigate("ListsS");
          }}
        />
          <Button
              title='SIGN OUT'
              containerViewStyle={style.buttonContainer}
              buttonStyle={style.button}
              onPress={
                  this.signOut.bind(this)
              }
          />
      </View>
    );
  }
}