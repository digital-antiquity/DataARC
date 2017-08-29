<!DOCTYPE html PUBLIC 
    "-//W3C//DTD XHTML 1.1 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <title>
            Indicator Creation Tool
        </title>
    <#include "/includes/header.ftl"/>

        <script data-main="/js/app/mapping/main" src="${contextPath}/components/requirejs/require.js"></script>
<style>

ul.options-list {
  display: flex;
  flex-direction: column;
  margin-top: -12px;
  border: 1px solid #dbdbdb;
  border-radius: 0 0 3px 3px;
  position: absolute;
  width: 100%;
  overflow: hidden;
}

ul.options-list li {
  width: 100%;
  flex-wrap: wrap;
  background: white;
  margin: 0;
  border-bottom: 1px solid #eee;
  color: #363636;
  padding: 7px;
  cursor: pointer;
}

ul.options-list li.highlighted {
  background: #f8f8f8
}

</style>
    <#import "/macros/body.ftl" as body />
    </head>
    
    <@body.body>
        <h1 class="page-header">Indicator Mapping</h1>
        <div class="container-fluid" id="schema">

            <div class="row">
                <div class="col-sm-11">
                    <label for="datasource" class="control-label col-sm-3">Choose a Data Source:</label> 
                    <select v-model="currentSchema" id="datasource" class="form-control" >
                        <option v-for="(option, index) in schema" v-bind:value="index"> {{ option.name }} </option>
                    </select>
                </div>
                <div class="col-sm-1">
                    <span class="glyphicon glyphicon-question-sign" aria-hidden="true"
                      data-toggle="tooltip" title="Indicators are built from a single data source"></span>
                </div>
            </div>
            <div class="row" v-show="currentSchema != undefined">
                <div class="col-sm-11">
                        <label for="choose-indicator" class="control-label col-sm-3">Indicator:</label> 
                        <select v-model="currentIndicator" id="choose-indicator" class="form-control">
                            <optgroup label="Existing Indicators" v-if="indicators.length > 0 ">
                                <option v-for="(option, index) in indicators" v-bind:value="index"> {{ option.name }} </option>
                            </optgroup>
                            <option value="new">Create New Indicator</option>
                        </select>
                    <span  v-if="fields.length > 0 && currentIndicator === parseInt(currentIndicator) && currentIndicator > 0">
                            <label for="indicatorId" class="control-label col-sm-3">Indicator Id:</label> {{indicators[currentIndicator].id}}
                    </span>

                </div>
                <div class="col-sm-1">
                    <span class="glyphicon glyphicon-question-sign" aria-hidden="true"
                        data-toggle="tooltip" title="Select an exsting indicator, or create a new one" ></span>
                </div>
            </div>

            <div class="row"  v-if="fields.length > 0 && currentIndicator === parseInt(currentIndicator)">
            <div class="col-sm-11">
                    <label for="indicatorName" class="control-label col-sm-3">Indicator Name:</label>
                    <input id="indicatorName" name="indicatorName" v-model="indicators[currentIndicator].name" class="form-control"/>

                </div>
                <div class="col-sm-1">
                <span class="glyphicon glyphicon-question-sign" aria-hidden="true"
                        data-toggle="tooltip" title="Choose a unique title for your indicator" ></span>
                </div>
            </div>

            <div class="row" v-show="currentIndicator != undefined">
                <div class="col-sm-11 col-sm-offset-1">
                    <ul v-for="value in uniqueValues">
                        <li><b>{{ value.value }}</b> ({{value.occurrence }})</li>
                    </ul>
                </div>
            </div>
            
            
            <div class="row"  v-if="fields.length > 0 && currentIndicator === parseInt(currentIndicator)">
                <div class="col-sm-10 col-sm-offset-1">
                    <ul class="list-group" v-for="(part, rowNum) in indicators[currentIndicator].query.conditions">
                        <spart :rowindex="rowNum" :schema="schemaName" :fields="fields" :part="indicators[currentIndicator].query.conditions[rowNum]" :parts="indicators[currentIndicator].query.conditions"></spart>
                    </ul>
                </div>
                <div class="col-sm-1">
                <span class="glyphicon glyphicon-question-sign" aria-hidden="true"
                    data-toggle="popover" title="Indicator Queries" data-content="Queries for indicators can be simple or complex, and utilize the combination of fields and values"></span>
                </div>
            </div>

            <div class="row"  v-if="fields.length > 0 && currentIndicator === parseInt(currentIndicator)">
                <div class="col-sm-10 col-sm-offset-1">
                    
                </div>
                <div class="col-sm-1">
                <span class="glyphicon glyphicon-question-sign" aria-hidden="true"
                    data-toggle="popover" title="Indicator Queries" data-content="Queries for indicators can be simple or complex, and utilize the combination of fields and values"></span>
                </div>
            </div>

            <span class='debug hidden'>{{ indicators[currentIndicator] }}</span>

            <div class="row"  v-if="fields.length > 0 && currentIndicator === parseInt(currentIndicator)">
                <div class="col-sm-11">
                    <label for="chooseTopic" class="control-label col-sm-3">Assign Topic:</label>
                    <br/>
                    <!-- fixme: was indicators[currentIndicator].topicIdentifers[_idx]  -->
					<ul class="list-unstyled">
                        <li v-for="(ident, _idx) in selectedTopics"  >
                                <select id="chooseTopic" name='topic' v-model="selectedTopics[_idx]"  class="form-control">
                                    <option v-for="(topic, index) in topics"  v-bind:value="topic.identifier"> {{ topic.name }} </option>
                                </select>
                        <span v-show="_idx > 0">
                            <button class="btn btn-xs btn-default" v-on:click="removeTopic(_idx)">-</button>
                        </span>

                        <span v-show="_idx == selectedTopics.length -1">
                            <button class="btn btn-xs btn-default" v-on:click="addTopic()">+</button>
                        </span>

                        </li>
                    </ul>
                </div>
                <div class="col-sm-1">
                    <span class="glyphicon glyphicon-question-sign" aria-hidden="true"></span>
                </div>
            </div>
            <br/>
            <div class="row"  v-if="fields.length > 0 && currentIndicator === parseInt(currentIndicator)">
                <div class="col-sm-12">
                    <button class="btn btn-xs btn-default" v-on:click="runQuery()" v-bind:disabled='cannotSearch'>Search / Test Query</button>
                    <button class="btn btn-xs btn-success" v-on:click="saveIndicator()" v-bind:disabled='cannotSubmit'>Save Indicator</button>
                    <button class="btn btn-xs btn-danger" v-on:click="deleteIndicator()" >Delete Indicator</button>
                </div>
            </div>
            <br/>
                <table v-if="results != undefined" class="table">
                <thead>
                    <tr>
                        <td colspan=10">Search Results: {{ results.length }}</td>
                    </tr>
                    <tr>
                        <th>id</th>
                        <th>source</th>
                        <th>start</th>
                        <th>end</th>
                        <th>link</th>
                        <th>raw</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-for="result in results" class="table">
                        <td>{{result.id}}</td>
                        <td>{{result.properties.source}}</td>
                        <td>{{result.properties.Start}}</td>
                        <td>{{result.properties.End}}</td>
                        <td><a v-show="result.properties['Link'] != undefined && result.properties['Link'] != ''" target="_blank" v-bind:href="result.properties['Link']"><span class="glyphicon glyphicon-link" aria-hidden="true"></span></a></td>
                        <td><textarea>{{result.properties | json}}</textarea></td>
                    </tr>
                    </tbody>
                </table>
            </div>
            </div>
            
        <template id="spart-template">
            <div> 
                <select name='fieldName' v-model="part.fieldName"  v-on:change="updateTest()" class="form-control">
                    <option v-for="(field, index) in fields"  v-bind:value="field.name"> {{ field.displayName }} </option>
                </select>
                <select name='type' v-model="part.type" class="form-control">
                    <option v-for="(limit, index) in getLimits()" v-bind:value="limit.value"> {{ limit.text }} </option>
                </select>
                  <autocomplete-input @select="onOptionSelect" v-bind:type="getHtmlFieldType(part.fieldName)" v-bind:field="part.fieldName"  v-bind:schema="schema" >

                    <template slot="item" scope="option">
                      <article class="media">
                        <p>
                          <strong>{{ option.value }}</strong>  ({{option.occurrence}})
                        </p>
                      </article>
                    </template>
                  </autocomplete-input>

                
                <span v-show="rowindex > 0">
                <button class="btn btn-xs btn-default" v-on:click="removePart(rowindex)">-</button>
                </span>
                <span v-show="rowindex == parts.length -1">
                <button class="btn btn-xs btn-default" v-on:click="addPart()">+</button>
                </span>
            </div>
        </template>
        <template id="spart-template">
            hi {{part}}
        </template>

<template id="autocomplete-input-template" >
  <div class="autocomplete-input">
                    {{options}}
    <p class="control">
      <input  v-model="keyword" class="input is-large" placeholder="Search..." 
        @input="onInput($event.target.value)"  @focus="focus"
        @keyup.esc="isOpen = false" @blur="isOpen = false" @keydown.down="moveDown" @keydown.up="moveUp" 
        @keydown.enter="select" >
      <i class="fa fa-angle-down"></i>
          <ul v-show="isOpen" class="options-list">
        <li v-for="(option, index) in options" :class="{
          'highlighted': index === highlightedPosition
        }" @mouseenter="highlightedPosition = index" @mousedown="select">
        <slot name="item" :value="option.value" :occurrence="option.occurrence" :option="option">
      </li>
    </ul>
      
      
    </p>
  </div>
</template>



        </@body.body>
</html>