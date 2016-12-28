  <div class="container" id="schema">

        <div class="col-sm-12">
	      Schema: <select v-model="selectedSchema" >
	          <option v-for="(option, index) in schema" v-bind:value="index"> {{ option.name }} </option>
	      </select>
<!--	      Field: <select v-model="selectedField" >
	          <option v-for="(option, index) in fields" v-bind:value="index"> {{ option.name }} </option>
	      </select> -->
	      </div>
	        <div class="col-sm-12">
	      
	      <ul v-for="value in uniqueValues">
	       <li><b>{{ value.value }}</b> ({{value.occurrence }})</li>
	      </ul>
	   </div>
  <div class="container-fluid" v-show="fields.length > 0">
    <ul class="list-group" v-for="(part, rowNum) in queryParts">
    	 <div>
 	<select name='fieldName' v-model="queryParts[rowNum].fieldName" v-on:change="updateTest()">
          <option v-for="(field, index) in fields"  v-bind:value="field.name"> {{ field.name }} </option>
 	</select>

 	 	<select name='type' v-model="queryParts[rowNum].type">
	 		<option value="EQUALS">Equals</option>
	 		<option value="DOES_NOT_EQUAL">Does Not Equal</option>
	 		<option value="GREATER_THAN">Greater Than</option>
	 		<option value="LESS_THAN">Less Than</option>
	 		<option value="CONTAINS">Contains</option>
	 	</select>
 	<input name='value' value="" v-bind:type="getHtmlFieldType(queryParts[rowNum].fieldName)" v-model="queryParts[rowNum].value"/>
 	<span v-show="rowNum > 0">
 	            <button class="btn btn-xs btn" v-on:click="removePart(rowNum)">-</button>
	</span>
 	<span v-show="rowNum == queryParts.length -1">
 	            <button class="btn btn-xs btn" v-on:click="addPart()">+</button>
	</span>
	</ul>
  </div>

	<label>Indicator Name</label><input name="indicatorName" v-model="indicatorName" />
            <button class="btn btn-xs btn" v-on:click="runQuery()">Search</button>
            <button class="btn btn-xs btn" v-on:click="saveIndicator()">Save Indicator</button>

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
  
  <script src="/js/app/mapping/mapping.js"></script>


<!--
 -->

<template id="spart-template">
hi {{part}}
</template>