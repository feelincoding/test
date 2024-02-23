<template>
    <div class="container-fluid">
        <div class="row">
            <!-- export to Excel -->
            <div class="toolbar-item col-sm-3 col-md-2">
                <excel-export-button :exportService="this.exportService" :getFlex="getFlex" />
            </div>

            <!-- export to PDF -->
            <div class="toolbar-item col-sm-3 col-md-2">
                <button class="btn btn-default btn-block" @click="exportToPdf">Export To PDF</button>
            </div>
        </div>

        <!-- group panel -->
        <wj-group-panel :placeholder="'Drag columns here to create groups'" :initialized="groupPanelInitialized" />

        <!-- the grid -->
        <wj-flex-grid
            :autoGenerateColumns="false"
            :allowAddNew="true"
            :allowDelete="true"
            :allowPinning="'SingleColumn'"
            :newRowAtTop="true"
            :showMarquee="true"
            :selectionMode="'MultiRange'"
            :validateEdits="false"
            :itemsSource="data"
            :initialized="flexInitialized"
        >
            <wj-flex-grid-filter :filterColumns="['OrderId','ProductName','ProductId','Status','Qty','UserId',]" />

            <wj-flex-grid-column binding="OrderId" header="OrderId" width="2*" :isReadOnly="true" />
            <wj-flex-grid-column binding="ProductName" header="ProductName" width="2*" :isReadOnly="true" />
            <wj-flex-grid-column binding="ProductId" header="ProductId" width="2*" :isReadOnly="true" />
            <wj-flex-grid-column binding="Status" header="Status" width="2*" :isReadOnly="true" />
            <wj-flex-grid-column binding="Qty" header="Qty" width="2*" :isReadOnly="true" />
            <wj-flex-grid-column binding="UserId" header="UserId" width="2*" :isReadOnly="true" />
        </wj-flex-grid>
    </div>
</template>

<script>
import '@grapecity/wijmo.touch';
import '@grapecity/wijmo.vue2.grid';
import '@grapecity/wijmo.vue2.grid.grouppanel';
import '@grapecity/wijmo.vue2.grid.filter'
import '@grapecity/wijmo.vue2.input';
import * as wjCore from '@grapecity/wijmo';
import { InputDate, InputTime } from "@grapecity/wijmo.input";
import { DataMap } from '@grapecity/wijmo.grid';
import { CellMaker, SparklineMarkers } from '@grapecity/wijmo.grid.cellmaker';
import { DataService } from "./data";
import { ExportService } from "./export";
import * as wjcCore from "@grapecity/wijmo";

export default {
    el: "#Grid",
    props: {
        employeesData: Array,
    },
    data: function() {
        return {
            data: null,

            // editors
            editors: {
                inputDate: new InputDate(document.createElement('div'), {
                    format: 'MM/dd/yyyy',
                    isRequired: false
                }),
                inputTime: new InputTime(document.createElement('div'), {
                    format: 'HH:mm',
                    isRequired: false
                })
            }
        };
    },
    created: function () {
        this.dataService = new DataService();
        this.exportService = new ExportService();
        // initializes data maps
        this._productMap = this._buildDataMap(this.dataService.getProducts());
        this._countryMap = new DataMap(this.dataService.getCountries(), 'id', 'name');
        this._colorMap = this._buildDataMap(this.dataService.getColors());
        // initializes cell templates
        this.historyCellTemplate = CellMaker.makeSparkline({
            markers: SparklineMarkers.High | SparklineMarkers.Low,
            maxPoints: 25,
            label: 'price history',
        });
        this.ratingCellTemplate = CellMaker.makeRating({
            range: [1, 5],
            label: 'rating'
        });
    },
    beforeDestroy() {
        this.exportService.cancelExcelExport();
    },
    methods: {
        flexInitialized: function(flexgrid) {
            this.flex = flexgrid;
            // sort the data by country
            let sd = new wjcCore.SortDescription("country", true);
            flexgrid.collectionView.sortDescriptions.push(sd);
            flexgrid.collectionView.currentChanged.addHandler(
                this._updateCurrentInfo.bind(this)
            );
            this._updateCurrentInfo();
        },
        getData: function() {
            var me = this
            // create some random data
            let countries = me.employeesData,
                data = [];
            for (let i = 0; i < countries.length; i++) {
                data.push({
                    id: i,
                    OrderId: countries[i].orderId,
                    ProductName: countries[i].productName,
                    ProductId: countries[i].productId,
                    Status: countries[i].status,
                    Qty: countries[i].qty,
                    UserId: countries[i].userId,
                });
            }
            return data;
        },
        groupPanelInitialized: function (ctl) {
            this.groupPanel = ctl;
            if (this.flex) {
                this.groupPanel.grid = this.flex;
            }
        },
        exportToPdf: function() {
            this.exportService.exportToPdf(this.flex, {
                countryMap: this._countryMap,
                colorMap: this._colorMap,
                historyCellTemplate: this.historyCellTemplate
            });
        },
        getChangeCls: function (value) {
            if (wjCore.isNumber(value)) {
                if (value > 0) {
                    return 'change-up';
                }
                if (value < 0) {
                    return 'change-down';
                }
            }
            return '';
        },
        _createItemsSource(counter) {
            const data = this.dataService.getData(counter);
            const view = new wjCore.CollectionView(data, {
                getError: (function (item, prop) {
                    const displayName = this.flex.columns.getColumn(prop).header;
                    return this.dataService.validate(item, prop, displayName);
                }).bind(this)
            });
            view.collectionChanged.addHandler((function (s, e) {
                // initializes new added item with a history data
                if (e.action === wjCore.NotifyCollectionChangedAction.Add) {
                    e.item.history = this.dataService.getHistoryData();
                    e.item.id = this._lastId;
                    this._lastId ++;
                }
            }).bind(this));
            return view;
        },
        // build a data map from a string array using the indices as keys
        _buildDataMap: function (items) {
            const map = [];
            for (let i = 0; i < items.length; i++) {
                map.push({ key: i, value: items[i] });
            }
            return new DataMap(map, 'key', 'value');
        },
        getFlex: function () {
            return this.flex;
        },
    },
    computed: {
        productMap: function () {
            return this._productMap;
        },
        countryMap: function () {
            return this._countryMap;
        },
        colorMap: function () {
            return this._colorMap;
        },
    },
    watch: {
        employeesData: function (){
            this.data = this.getData()
        },
    },
    filters: {
        safeCurrency: function (value) {
            if (wjCore.isNumber(value)) {
                return wjCore.Globalize.formatNumber(value, 'c');
            }
            if (!wjCore.isUndefined(value) && value !== null) {
                return wjCore.changeType(value, wjCore.DataType.String);
            }
            return '';
        }
    },
}
</script>

<style>
    @import "~@grapecity/wijmo.styles/wijmo.css";
    @import "~bootstrap/dist/css/bootstrap.css";
    @import url('https://cdnjs.cloudflare.com/ajax/libs/flag-icon-css/3.1.0/css/flag-icon.css');

    body {
        font-size:1.5em;
        font-family: -apple-system,BlinkMacSystemFont,"Segoe UI Light",Roboto,Oxygen-Sans,Ubuntu,Cantarell,"Helvetica Neue",sans-serif;
    }

    .toolbar-item {
        margin-bottom: 6px;
    }

    .wj-flexgrid {
        height: 330px;
    }

    .wj-flexgrid .wj-cell {
        padding: 7px;
        border:none;
    }

    .wj-cell.wj-state-invalid:not(.wj-header)::after {
        top: -14px;
        border: 14px solid transparent;
        border-right-color: red;
    }

    .flag-icon {
        box-shadow: 1px 1px 4px rgba(0,0,0,0.4);
    }

    .color-tile {
        display: inline-block;
        position: relative;
        width: 1em;
        height: 1em;
        border-radius: 50%;
        box-shadow: 1px 1px 4px rgba(0,0,0,0.4);
        vertical-align: middle;
    }

    .change-up {
        color: darkgreen;
    }
    .change-up:after {
        content: '\25b2';
    }
    .change-down {
        color: darkred;
    }
    .change-down:after {
        content: '\25bc';
    }

    .cell-rating {
        font-size: 12px;
    }

    .wj-flexgrid .wj-detail {
        padding: 4px 16px;
    }

    .wj-detail h3 {
        margin:10px 0;
    }
</style>