import React, { Component } from 'react';
import { StyleSheet, View, Text, TouchableHighlight } from 'react-native';
import ListViewSelect from 'react-native-list-view-select';

export default class ListViewSelect extends Component {

    constructor(props) {
        super(props);
        this.state = {
            amount: "Select Amount",
            isVisible: false,
        };
        _.bindAll(this, ['showPopover', 'closePopover', 'setAmount']);
    }

    showPopover() {
        this.setState({isVisible: true});
    }

    closePopover() {
        this.setState({isVisible: false});
    }

    setAmount(amount) {
        this.setState({ amount: amount });
    }

    render() {
        const { selectedAmout } = this.props;
        const amounts = [
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            "10",
        ];

        return (
            <View style={styles.container}>
                <TouchableHighlight onPress={this.showPopover}>
                    <Text>{this.state.amount}</Text>
                </TouchableHighlight>
                <ListViewSelect
                    list={amounts}
                    isVisible={this.state.isVisible}
                    onClick={this.setAmount}
                    onClose={this.closePopover}
                />
            </View>
        );
    }
}

const styles = StyleSheet.create({
    container: {
        paddingTop: 100,
        paddingBottom: 100,
    },
});