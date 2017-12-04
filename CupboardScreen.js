import React, { Component } from 'react';
import {
  Text,
  View,
  StyleSheet,
  TextInput,
  Alert,
  ScrollView
} from 'react-native';
import { Button } from 'react-native-elements';
import Accordion from 'react-native-collapsible/Accordion';
//https://www.npmjs.com/package/react-native-collapsible
import { SearchBar } from 'react-native-elements';
import { style } from './Styles';
//https://react-native-training.github.io/react-native-elements/API/searchbar/

//TODO:: put in search functionality
//TODO:: add 'edit item' and 'add to list' button functionality
//TODO:: Read in data from user/firebase to SECTIONS
//TODO:: Look into accordian separators
//TODO:: add "sort by" button at the top

const SECTIONS = [ //PLACEHOLDER. THIS WILL BE READ IN FROM USER/FIREBASE DATA.
  {
    title: 'Cereal1',
    noItems: '2',
    content: 'Calories - 250\nExpires - 10/20/17',
  },
  {
    title: 'Baked beans',
    noItems: '3',
    content: 'Calories - 20\nExpires - 10/21/17',
  },
  {
      title: 'Cereal2',
      noItems: '2',
      content: 'Calories - 250\nExpires - 10/20/17',
  },
  {
      title: 'Cereal3',
      noItems: '2',
      content: 'Calories - 250\nExpires - 10/20/17',
  },
  {
      title: 'Cereal4',
      noItems: '2',
      content: 'Calories - 250\nExpires - 10/20/17',
  },
  {
      title: 'Cereal5',
      noItems: '2',
      content: 'Calories - 250\nExpires - 10/20/17',
  },
  {
      title: 'Cereal6',
      noItems: '2',
      content: 'Calories - 250\nExpires - 10/20/17',
  },
];

export default class CupboardScreen extends Component<{}> {
  constructor(props) {
    super(props);
      this.state = {
        quantity: []
      };
      //this.onIncrementPress = this.onIncrementPress.bind(this)
      this.onAddToList = this.onAddToList.bind(this);
      this.EditItem = this.EditItem.bind(this);
      this.deleteItem = this.deleteItem.bind(this);
      this._renderHeader = this._renderHeader.bind(this);
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

  deleteItem(index) {
    delete(SECTIONS[index]);
  }

  onChanged(text,arrayvar){
      let newText = '';
      let numbers = '0123456789';
      for (let i=0; i < text.length; i++) {
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

  _renderHeader(section,index) {
    let arrayvar = this.state.quantity.slice();
    arrayvar.push(section.noItems);
    return (
      <View style={style.accordianHeader}>
        <Text style={style.accordianHeader}>{section.title}</Text>
        <TextInput
          style={style.smallerTextInput}
          keyboardType='numeric'
          onChangeText={(text)=> this.onChanged(text,arrayvar)}
          placeholder={section.noItems.toString()}
          value={this.state.quantity}
          maxLength={100}  //setting limit of input
        />
        <Button //SHOULD BE SWIPE TO DELETE, IS BUTTON FOR NOW
            containerViewStyle={style.buttonContainer}
            buttonStyle={style.button}
            backgroundColor="#ffffff"
            onPress={() => {
              this.deleteItem(index)
            }}

            title="Delete Item"
            color="black"
            raised
        />
      </View>
    );
  }

  _renderContent(section) {
    return (
      <View style={style.containerCenterContent}>
        <Text style={style.accordianHeader}>{section.content}</Text>
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
      <View style={style.content}>
        <ScrollView>
          <SearchBar
            round
            //onChangeText={filterResults}
            //onClearText={}
            placeholder='Type Here...' />
          <Accordion
            sections={SECTIONS}
            renderHeader={this._renderHeader}
            renderContent={this._renderContent}
            onChange={() => {
                this.render();
            }}
          />
        </ScrollView>
      </View>
    );
  }
}