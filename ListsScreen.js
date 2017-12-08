import React, { Component } from 'react';
import {
  View,
  FlatList,
  TouchableOpacity
} from 'react-native';
import { List, ListItem, Button, Icon} from 'react-native-elements'
import { style } from "./Styles";

export default class ListsScreen extends Component<{}> {
  constructor(props) {
    super(props);

    this.state = {
      data: null,
    };
  }

  // buttons for the header
  static navigationOptions = ({navigation}) => ({
    headerRight: // icons on the right
      <View style={{flexDirection: "row"}}>

        <Icon // Button to create a list
          color="#739E82"
          name="plus"
          type="entypo"
          containerStyle={{
            marginRight: 15
          }}
          onPress={()=>{
            // Create a new empty list titled "My List" (temporarily)
            const newList = navigation.state.params.self.createList('My List');
            // navigate to the next screen passing list data and the fbhandler
            navigation.navigate("CheckListS", {
              list: newList,
              fbhandler: navigation.state.params.fbhandler
            });
          }}
        />
        <Icon // sync with firebase button
          color="#739E82"
          name="sync"
          type="octicons"
          containerStyle={{
            marginRight: 15
          }}
          onPress={()=>{
            navigation.state.params.self._refreshLists();
          }}
        />
      </View>
  });


  // creates a list based on a name and puts in it firebase
  // name: string
  createList(name) {
    const fbhandler = this.props.navigation.state.params.fbhandler;
    const ref = fbhandler.createList(name); // create in firebase, ref is a string for id.

    // get the current data and push the new list onto it
    let newData = this.state.data;
    const newList = {
      key: ref.key,
      title: name
    };
    newData.push(newList);
    this.setState({
      data: newData
    });

    return newList;
  }

  // deletes an element based on listid.
  // listid: string
  async deleteList(listid) {
    const fbhandler = this.props.navigation.state.params.fbhandler;
    const ref = fbhandler.deleteList(); // delete from firebase

    // find arr index to delete
    let indexToDelete = null;
    this.state.data.forEach((elem, idx)=>{
      if (elem.key === listid){
        indexToDelete = idx;
      }
    });

    // if the index isnt null, delete
    if (indexToDelete !== null) {
      await fbhandler.deleteList(listid); // delete from firebase

      // update the state with the new data.
      let newData = this.state.data;
      newData.splice(indexToDelete, 1);
      this.setState({
        data: newData
      });
    }
  }


  // refresh the lists
  async _refreshLists(){
    const fbhandler = this.props.navigation.state.params.fbhandler;
    const lists = await fbhandler.getLists(); // get all the data for the lists

    const data = [];

    // make sure the list exists
    if (lists.val()){
      Object.keys(lists.val()).forEach(function(key) {
        // push the data objects to our temp data array
        data.push({
          title: lists.val()[key].title,
          key: key
        });
      });

      // set the state to our new (or same) data
      this.setState({
        data: data
      });

    } else {
      // if the user has no lists, set the state to an empty array
      this.setState({
        data: []
      });
    }
  }

  // Every time the component mounts
  componentDidMount(){
    // This is a workaround to get the plus button to have access to THIS.
    this.props.navigation.setParams({
      self: this
    });
    this._refreshLists(); // get the lists and display them
  }

  render() {
    return (
      <View>
        <FlatList
          data={this.state.data}
          renderItem={({item}) => <ListItem
            onPress={()=>{
              // pass list data, the fbhandler, and the previous screen context
              this.props.navigation.navigate("CheckListS", {
                list: item,
                fbhandler: this.props.navigation.state.params.fbhandler,
                prevScreen: this // NOTE: this is important for sending data backwards.
              });
            }}
            key={item.key}
            title={item.title}
            onLongPress={()=>{
              this.deleteList(item.key);
            }}
          />}
          keyExtractor={(item, index) => item.key}
          extraData={this.state}
        />
      </View>
    );
  }
}