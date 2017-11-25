import React, { Component } from 'react';
import {
  Text,
  View,
  StyleSheet
} from 'react-native';
import { Button } from 'react-native-elements';
import { style } from "./Styles";

export default class HomeScreen extends Component<{}> {
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
      </View>
    );
  }
}