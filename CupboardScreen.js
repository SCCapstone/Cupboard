import React, { Component } from 'react';
import {
  Text,
  View,
  StyleSheet,
  TextInput,
  Alert
} from 'react-native';
import { style } from './Styles'
import { Button } from 'react-native-elements'
import Accordion from 'react-native-collapsible/Accordion'
//https://www.npmjs.com/package/react-native-collapsible
import { SearchBar } from 'react-native-elements'
//https://react-native-training.github.io/react-native-elements/API/searchbar/

//TODO:: put in search functionality
//TODO:: implement deleting of rows
//TODO:: add 'edit item' and 'add to list' button functionality
//TODO:: Read in data from user/firebase to SECTIONS
//TODO:: Look into accordian separators


const SECTIONS = [ //PLACEHOLDER. THIS WILL BE READ IN FROM USER/FIREBASE DATA.
  {
    title: 'Cereal',
    noItems: '2',
    content: 'Calories - 250\nExpires - 10/20/17',
  },
  {
    title: 'Baked beans',
    noItems: '3',
    content: 'Calories - 20\nExpires - 10/21/17',
  }
];

export default class CupboardScreen extends Component<{}> {
  constructor(props) {
    super(props);
      this.state = {
        quantity: []
      };
      //this.onIncrementPress = this.onIncrementPress.bind(this)
      this.onAddToList = this.onAddToList.bind(this)
      this.EditItem = this.EditItem.bind(this)
      this.deleteItem = this.deleteItem.bind(this)
      this._renderHeader = this._renderHeader.bind(this)
  }

  /*
  filterResults() {
    //TODO:: add filtering of list for search function
  }
  */

  onAddToList() {
    //TODO:: implement this function
  }

  EditItem() {
    //edit item should be the exact same as manual entry, just renamed to 'edit a food' rather than 'add a food'
    //TODO:: implement this function
  }

  deleteItem() {
    //TODO:: implement this function
  }

  onChanged(text,arrayvar){
      let newText = '';
      let numbers = '0123456789';
      for (var i=0; i < text.length; i++) {
          if(numbers.indexOf(text[i]) > -1 ) {
              newText = newText + text[i];
          }
          else {
              // your call back function
              alert("please enter numbers only");
          }
      }
      this.setState({ quantity: arrayvar });
  }

  _renderHeader(section) {
    var arrayvar = this.state.quantity.slice()
    arrayvar.push(section.noItems)
    return (
      <View style={styles.containerCenterContent}>
        <Text>{section.title}</Text>
        <TextInput
          //style={styles.textInput}
          keyboardType='numeric'
          onChangeText={(text)=> this.onChanged(text,arrayvar)}
          placeholder={section.noItems.toString()}
          value={this.state.quantity}
          maxLength={10}  //setting limit of input
        />
        <Button //SHOULD BE SWIPE TO DELETE, IS BUTTON FOR NOW
            containerViewStyle={style.buttonContainer}
            buttonStyle={style.button}
            backgroundColor="#ffffff"
            onPress={
              this.deleteItem
            }
            title="Delete Item"
            color="black"
            raised
        />
      </View>
    );
  }

  _renderContent(section) {
    return (
      <View style={styles.containerCenterContent}>
        <Text>{section.content}</Text>
          <View style={style.accordianButtons}>
          <Button
              containerViewStyle={style.buttonContainer}
              buttonStyle={style.button}
              backgroundColor="#ffffff"
              onPress={
                  this.onAddToList
              }
              title="Add to List"
              color="black"
              raised
          />
          <Button
          containerViewStyle={style.buttonContainer}
          buttonStyle={style.button}
          backgroundColor="#ffffff"
          onPress={
              this.EditItem
          }
          title="Edit Item"
          color="black"
          raised
      />
          </View>
      </View>
    );
  }

  render() {
    return (
      <View style={styles.content}>
      <SearchBar
        round
        //onChangeText={filterResults}
        //onClearText={}
        placeholder='Type Here...' />
      <Accordion
        sections={SECTIONS}
        renderHeader={this._renderHeader}
        renderContent={this._renderContent}
      />
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
  },
});