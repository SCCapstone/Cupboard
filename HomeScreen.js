import React, { Component } from 'react';
import {Text, View} from 'react-native';
import { Button } from 'react-native-elements'

export default class HomeScreen extends Component<{}> {
    render() {
        return (
            <View>
                <Text>Sample</Text>
                <Button title='LARGE BUTTON' />
            </View>
        );
    }
}