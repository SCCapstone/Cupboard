import React, { Component } from 'react';
import {
  Text,
  View,
  StyleSheet
} from 'react-native';
import { Button } from 'react-native-elements'

export default class ListsScreen extends Component<{}> {
  render() {
    return (
      <View style={styles.page}>
        <Text>This is the Shopping Lists Screen.</Text>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  page: {
    flexDirection: "column",
    justifyContent: "center",
    alignItems: "center",
    height: "100%",
  }
});