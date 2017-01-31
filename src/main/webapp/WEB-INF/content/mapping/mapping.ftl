  <div class="container" id="schema">

        <div class="col-sm-12">
	      Schema: <select v-model="currentSchema" >
	          <option v-for="(option, index) in schema" v-bind:value="index"> {{ option.name }} </option>
	      </select>
	      <span v-show="currentSchema != undefined">

	       Existing: <select v-model="currentIndicator">
	          <optgroup label="Existing Indicators" v-if="indicators.length > 0 ">
	          <option v-for="(option, index) in indicators" v-bind:value="index"> {{ option.name }} </option>
	          </optgroup>
	          <option value="new">New Indicator</option>
	      </select>
	      </span>
	      </div>
	        <div class="col-sm-12">
	      
	      <ul v-for="value in uniqueValues">
	       <li><b>{{ value.value }}</b> ({{value.occurrence }})</li>
	      </ul>
	   </div>
	   
  <div class="container-fluid" v-if="fields.length > 0 && currentIndicator === parseInt(currentIndicator)">
    <ul class="list-group" v-for="(part, rowNum) in indicators[currentIndicator].query.conditions">
    	 <spart :rowindex="rowNum" :fields="fields" :part="indicators[currentIndicator].query.conditions[rowNum]" :parts="indicators[currentIndicator].query.conditions"></spart>
	</ul>

    <label>Indicator Name</label><input name="indicatorName" v-model="indicators[currentIndicator].name" />
    <label>Indicator Id</label> {{indicators[currentIndicator].id}}


        <select name='topic' v-model="indicators[currentIndicator].topicIdentifier">
          <option v-for="(topic, index) in topics"  v-bind:value="topic.identifier"> {{ topic.name }} </option>
        </select>

                        <button class="btn btn-xs btn" v-on:click="runQuery()">Search</button>
            <button class="btn btn-xs btn" v-on:click="saveIndicator()">Save Indicator</button>
  </div>

	<table v-if="results != undefined" >
	<tr><td colspan=10">{{ results.length }}</td></tr>
	<tr>
		<th>id</th>
		<th>source</th>
		<th>start</th>
		<th>end</th>
		<th>link</th>
		<th>raw</th>
		</tr>
		<tr v-for="result in results" class="table">
			<td>{{result.id}}</td>
			<td>{{result.properties.source}}</td>
			<td>{{result.properties.Start}}</td>
			<td>{{result.properties.End}}</td>
			<td>{{result.properties['Link']}}</td>
			<td><textarea>{{result.properties | json}}</textarea></td>
		</tr>
	</table>
  </div>

</div>
  
  
  <template id="spart-template">
  	<div>
  	 	<select name='fieldName' v-model="part.fieldName"  v-on:change="updateTest()">
          <option v-for="(field, index) in fields"  v-bind:value="field.name"> {{ field.name }} </option>
        </select>

 	 	<select name='type' v-model="part.type">
	 		<option value="EQUALS">Equals</option>
	 		<option value="DOES_NOT_EQUAL">Does Not Equal</option>
	 		<option value="GREATER_THAN">Greater Than</option>
	 		<option value="LESS_THAN">Less Than</option>
	 		<option value="CONTAINS">Contains</option>
	 	</select>
 	<input name='value' value="" v-bind:type="getHtmlFieldType(part.fieldName)" v-model="part.value"/>
 	<span v-show="rowindex > 0">
 	            <button class="btn btn-xs btn" v-on:click="removePart(rowindex)">-</button>
	</span>
 	 <span v-show="rowindex == parts.length -1">
 	            <button class="btn btn-xs btn" v-on:click="addPart()">+</button>
	</span>
  	  	</div>
  
  </template>
  
  <script src="/js/app/mapping/mapping.js"></script>


<!--
 -->

<template id="spart-template">
hi {{part}}
</template>