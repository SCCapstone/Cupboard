import React, { Component } from 'react';
import {
  Text,
  View,
  Alert
} from 'react-native';
import { Button } from 'react-native-elements';
import { style } from "./Styles";

export default class EntryScreen extends Component<{}> {
  render() {
    return (
      <View style={style.containerCenterContent}>
        <Button
          containerViewStyle={style.buttonContainer}
          buttonStyle={style.button}
          title='USELESS BUTTON'
        />
      </View>
    );
  }
}