<template>

    <v-card outlined>
        <v-card-title>
            DecreaseProduct
        </v-card-title>

        <v-card-text>
            <Number label="Stock" v-model="value.stock" :editMode="editMode"/>
            <String label="OrderId" v-model="value.orderId" :editMode="editMode"/>
        </v-card-text>

        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
                    color="deep-purple lighten-2"
                    text
                    @click="decreaseProduct"
            >
                DecreaseProduct
            </v-btn>
            
            <v-btn
                    color="deep-purple lighten-2"
                    text
                    @click="close"
            >
                Close
            </v-btn>
        </v-card-actions>
    </v-card>

</template>

<script>
   
    export default {
        name: 'DecreaseProductCommand',
        components:{},
        props: {},
        data: () => ({
            editMode: true,
            value: {},
        }),
        created() {
            this.value.stock = 0;
            this.value.orderId = '';
        },
        watch: {
        },
        methods: {
            decreaseProduct() {
                this.$emit('decreaseProduct', this.value);
            },
            close() {
                this.$emit('closeDialog');
            },
            change() {
                this.$emit('input', this.value);
            },
        }
    }
</script>

