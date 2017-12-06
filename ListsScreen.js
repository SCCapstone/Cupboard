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

  static navigationOptions = ({navigation}) => ({
    headerRight:
      <View style={{flexDirection: "row"}}>
        <Icon
          color="#739E82"
          name="plus"
          type="entypo"
          containerStyle={{
            marginRight: 15
          }}
          onPress={()=>{
            const newList = navigation.state.params.self.createList('My List');
            navigation.navigate("CheckListS", {
              list: newList,
              fbhandler: navigation.state.params.fbhandler
            });
          }}
        />
        <Icon
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


  // creates a list based on a name you give it, puts in it firebase too.
  createList(name) {
    const fbhandler = this.props.navigation.state.params.fbhandler;
    const ref = fbhandler.createList(name);

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
  async deleteList(listid) {
    const fbhandler = this.props.navigation.state.params.fbhandler;
    const ref = fbhandler.deleteList();

    //find arr index
    let indexToDelete = null;

    this.state.data.forEach((elem, idx)=>{
      if (elem.key === listid){
        indexToDelete = idx;
      }
    });

    if (indexToDelete !== null) {
      await fbhandler.deleteList(listid);

      let newData = this.state.data;
      newData.splice(indexToDelete, 1);

      this.setState({
        data: newData
      });
    }
  }

  async _refreshLists(){
    const fbhandler = this.props.navigation.state.params.fbhandler;
    const lists = await fbhandler.getLists();

    const data = [];

    if (lists.val()){
      Object.keys(lists.val()).forEach(function(key) {
        data.push({
          title: lists.val()[key].title,
          key: key
        });
      });

      this.setState({
        data: data
      });

    } else {
      this.setState({
        data: []
      });
    }
  }

  componentDidMount(){
    // This is a workaround to get the plus button to have access to THIS.
    this.props.navigation.setParams({
      self: this
    });

    this._refreshLists();
  }

  render() {
    return (
      <View>
        <FlatList
          data={this.state.data}
          renderItem={({item}) => <ListItem
            onPress={()=>{
              this.props.navigation.navigate("CheckListS", {
                list: item,
                fbhandler: this.props.navigation.state.params.fbhandler,
                prevScreen: this
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