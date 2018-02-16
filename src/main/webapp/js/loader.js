/**
 * Loader class for data-arc
 * Requires ECMA6, jQuery
 */

class LoaderHandler {
  constructor() {
    this.page_loader = $('<div>', {
      'class': 'd-flex h-100 align-items-center loader',
      'style': 'position:fixed; top:0; right:0; bottom:0; left:0; background-color:rgba(0,0,0,0.90); z-index:99998;'
    }).append('<div class="mx-auto"><span class="fa text-light fa-cog fa-spin fa-5x"></span></div></div>');
    this.large = '<div class="d-flex h-100 align-items-center loader"><div class="mx-auto"><span class="fa fa-cog fa-spin fa-4x"></span></div></div>';
    this.medium = '<div class="d-flex h-100 align-items-center loader"><div class="mx-auto"><span class="fa fa-cog fa-spin fa-2x"></span></div></div>';
    this.small = '<div class="d-flex h-100 align-items-center loader"><div class="mx-auto"><span class="fa fa-cog fa-spin fa-1x"></span></div></div>';
  }
  show() {
    $('body').append(this.page_loader);
  }
  hide() {
    this.page_loader.remove();
  }
}