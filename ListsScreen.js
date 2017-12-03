import React, { Component } from 'react';
import {
  Text,
  View,
  StyleSheet,
  ListView,
  FlatList,
  TouchableOpacity
} from 'react-native';
import { List, ListItem, Button } from 'react-native-elements'
import { style } from "./Styles";

export default class ListsScreen extends Component<{}> {
  constructor(props) {
    super(props);

    this.state = {
      data: null,
    };
  }

  createList() {
    const fbhandler = this.props.navigation.state.params.fbhandler;
    const ref = fbhandler.createList('test1');

    let newData = this.state.data;

    newData.push({
      key: ref.key,
      title: "test1"
    });

    this.setState({
      data: newData
    });
  }

  // TODO implement delete function.
  deleteList() {
    const fbhandler = this.props.navigation.state.params.fbhandler;
    const ref = fbhandler.deleteList();

    let newData = this.state.data;
    newData.shift();

    this.setState({
      data: newData
    });
  }

  async componentDidMount(){
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

  render() {
    return (
      <View>
        <Button
          onPress={()=>{this.createList()}}
        />
        <FlatList
          data={this.state.data}
          renderItem={({item}) => <ListItem
            onPress={()=>{
              this.props.navigation.navigate("CheckListS", {
                list: item,
                fbhandler: this.props.navigation.state.params.fbhandler
              });
            }}
            key={item.key}
            title={item.title}
          />}
          keyExtractor={(item, index) => item.key}
          extraData={this.state}
        />
      </View>
    );
  }
}